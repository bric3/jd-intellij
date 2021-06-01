package jd.ide.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.util.Clock;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This service offers support to refresh the content of decompiled files on configuration change.
 */
public class JavaDecompilerRefreshSupportService {
    private static final Logger LOGGER = Logger.getInstance(JavaDecompilerRefreshSupportService.class);

    private final ConcurrentHashMap<WeakReference<VirtualFile>, Long> decompiledFiles =
            new ConcurrentHashMap<>();

    private final List<JavaDecompilerRefreshListener> refreshListeners =
            new CopyOnWriteArrayList<>();

    public void markDecompiled(VirtualFile virtualFile) {
        decompiledFiles.put(new WeakReference<>(virtualFile), Clock.getTime());
    }


    public void refreshDecompiledFiles() {
        for (JavaDecompilerRefreshListener refreshListener : refreshListeners) {
            refreshListener.onRefreshDecompiledFiles();
        }
        ApplicationManager.getApplication().invokeLater(new RefreshDecompiledFilesTask());

    }

    public void registerRefreshListener(JavaDecompilerRefreshListener refreshListener) {
        refreshListeners.add(refreshListener);
    }

    private class RefreshDecompiledFilesTask implements Runnable {
        @Override
        public void run() {
            FileDocumentManagerImpl documentManager = (FileDocumentManagerImpl) FileDocumentManager.getInstance();

            LOGGER.warn("entries : " + decompiledFiles.size());

            final Set<Map.Entry<WeakReference<VirtualFile>, Long>> entries =
                    new HashSet<>(decompiledFiles.entrySet());
            decompiledFiles.clear();

            for (Map.Entry<WeakReference<VirtualFile>, Long> virtualFileWeakReference : entries) {
                VirtualFile virtualFile = virtualFileWeakReference.getKey().get();
                if (virtualFile != null) {
                    final var oldModificationTimestamp = virtualFileWeakReference.getValue();
                    final var newModificationStamp = Clock.getTime();
                    LOGGER.warn("[JD] contentsChanged on : " + virtualFile.getPresentableUrl() + "old: " + oldModificationTimestamp + " new: " + newModificationStamp);
                    documentManager.contentsChanged(new VFileContentChangeEvent(
                            null,
                            virtualFile,
                            oldModificationTimestamp,
                            newModificationStamp,
                            false
                    ));
                }

            }
        }
    }

    public interface JavaDecompilerRefreshListener {
        void onRefreshDecompiledFiles();
    }
}