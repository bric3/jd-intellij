package jd.ide.intellij;

import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This service offers support to refresh the content of decompiled files on configuration change.
 */
public class JavaDecompilerRefreshSupportService {
    private ConcurrentHashMap<WeakReference<VirtualFile>, WeakReference<VirtualFile>> decompiledFiles =
            new ConcurrentHashMap<WeakReference<VirtualFile>, WeakReference<VirtualFile>>();

    public void markDecompiled(VirtualFile virtualFile) {
        WeakReference<VirtualFile> weakRef = new WeakReference<VirtualFile>(virtualFile);
        decompiledFiles.put(weakRef, weakRef);
    }


    public void refreshDecompiledFiles() {
        LaterInvocator.invokeLater(new RefreshDecompiledFilesTask());
    }

    private class RefreshDecompiledFilesTask implements Runnable {
        @Override public void run() {
            FileDocumentManager documentManager = FileDocumentManager.getInstance();

            // need lock ?
            HashSet<WeakReference<VirtualFile>> weakReferences =
                    new HashSet<WeakReference<VirtualFile>>(decompiledFiles.keySet());
            decompiledFiles.clear();

            for (WeakReference<VirtualFile> virtualFileWeakReference : weakReferences) {
                VirtualFile virtualFile = virtualFileWeakReference.get();
                if (virtualFile != null) {
                    System.out.println("contentsChanged on : " + virtualFile.getPresentableUrl());
                    ((VirtualFileListener) documentManager).contentsChanged(
                            new VirtualFileEvent(null, virtualFile, virtualFile.getName(), virtualFile.getParent())
                    );
                }

            }
        }
    }
}
