package com.github.lsp4intellijplugin.ballerinaextension;

import com.github.lsp4intellijplugin.ballerinaextension.client.ExtendedRequestManager;
import com.github.lsp4intellijplugin.ballerinaextension.editoreventmanager.ExtendedEditorEventManager;
import com.github.lsp4intellijplugin.ballerinaextension.server.ExtendedLanguageServer;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseListener;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.wso2.lsp4intellij.client.languageserver.ServerOptions;
import org.wso2.lsp4intellij.client.languageserver.requestmanager.DefaultRequestManager;
import org.wso2.lsp4intellij.client.languageserver.requestmanager.RequestManager;
import org.wso2.lsp4intellij.client.languageserver.wrapper.LanguageServerWrapper;
import org.wso2.lsp4intellij.editor.EditorEventManager;
import org.wso2.lsp4intellij.editor.listeners.EditorMouseMotionListenerImpl;
import org.wso2.lsp4intellij.extensions.LSPExtensionManager;

public class BallerinaLSPExtensionManager implements LSPExtensionManager {

    @Override
    public <T extends DefaultRequestManager> T getExtendedRequestManagerFor(LanguageServerWrapper wrapper,
            LanguageServer server, LanguageClient client, ServerCapabilities serverCapabilities) {
        return (T) new ExtendedRequestManager(wrapper, server, client, serverCapabilities);
    }

    @Override
    public <T extends EditorEventManager> T getExtendedEditorEventManagerFor(Editor editor,
            DocumentListener documentListener, EditorMouseListener mouseListener,
            EditorMouseMotionListenerImpl mouseMotionListener, RequestManager requestManager,
            ServerOptions serverOptions, LanguageServerWrapper wrapper) {
        return (T) new ExtendedEditorEventManager(editor, documentListener, mouseListener, mouseMotionListener,
                requestManager, serverOptions, wrapper);
    }

    @Override
    public Class<? extends LanguageServer> getExtendedServerInterface() {
        return ExtendedLanguageServer.class;
    }
}
