/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.lsp4intellijplugin.ballerinaextension.editoreventmanager;

import com.github.lsp4intellijplugin.ballerinaextension.client.ExtendedRequestManager;
import com.github.lsp4intellijplugin.ballerinaextension.server.BallerinaServiceListRequest;
import com.github.lsp4intellijplugin.ballerinaextension.server.BallerinaServiceListResponse;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import org.eclipse.lsp4j.Position;
import org.wso2.lsp4intellij.client.languageserver.ServerOptions;
import org.wso2.lsp4intellij.client.languageserver.requestmanager.RequestManager;
import org.wso2.lsp4intellij.client.languageserver.wrapper.LanguageServerWrapper;
import org.wso2.lsp4intellij.editor.EditorEventManager;
import org.wso2.lsp4intellij.listeners.LSPCaretListenerImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExtendedEditorEventManager extends EditorEventManager {

    public ExtendedEditorEventManager(Editor editor, DocumentListener documentListener,
            EditorMouseListener mouseListener, EditorMouseMotionListener mouseMotionListener, LSPCaretListenerImpl caretListener,
            RequestManager requestManager, ServerOptions serverOptions, LanguageServerWrapper wrapper) {
        super(editor, documentListener, mouseListener, mouseMotionListener, caretListener, requestManager, serverOptions, wrapper);
    }

    @Override
    public Iterable<? extends LookupElement> completion(Position pos) {
        BallerinaServiceListRequest serviceRequest = new BallerinaServiceListRequest();
        serviceRequest.setDocumentIdentifier(super.getIdentifier());
        ExtendedRequestManager requestManager = (ExtendedRequestManager) super.getRequestManager();
        CompletableFuture<BallerinaServiceListResponse> responseFuture = requestManager.serviceList(serviceRequest);
        BallerinaServiceListResponse response = null;
        try {
            response = responseFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        String[] services = response.getServices();
        return super.completion(pos);
    }
}
