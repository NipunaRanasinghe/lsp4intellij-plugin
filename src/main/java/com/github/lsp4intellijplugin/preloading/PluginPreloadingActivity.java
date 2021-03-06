package com.github.lsp4intellijplugin.preloading;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition;

import java.nio.file.Paths;

import static com.github.lsp4intellijplugin.preloading.OperatingSystemUtils.getOperatingSystem;

public class PluginPreloadingActivity extends PreloadingActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginPreloadingActivity.class);
    private static final String launcherScriptPath = "lib/tools/lang-server/launcher";
    private static final String ballerinaSourcePath = "lib/repo";

    /**
     * Preloading of the ballerina plugin.
     */
    @Override
    public void preload(@NotNull ProgressIndicator indicator) {

        // Registers language server definitions for initially opened projects.
        registerServerDefinition();

        final MessageBusConnection connect = ApplicationManager.getApplication().getMessageBus().connect();
        connect.subscribe(ProjectManager.TOPIC, new ProjectManagerListener() {
            @Override
            public void projectOpened(@Nullable final Project project) {
                registerServerDefinition(project);
            }
        });
    }

    /**
     * Registered language server definition using currently opened ballerina projects.
     */
    private static void registerServerDefinition() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : openProjects) {
            registerServerDefinition(project);
        }
    }

    private static boolean registerServerDefinition(Project project) {
        //If the project does not have a ballerina SDK attached, ballerinaSdkPath will be null.
        String balSdkPath = getBallerinaSdk(project);
        return balSdkPath != null && doRegister(balSdkPath);
    }

    private static boolean doRegister(@NotNull String sdkPath) {
        String os = getOperatingSystem();
        if (os != null) {
            String path = null;
            if (os.equals(OperatingSystemUtils.UNIX) || os.equals(OperatingSystemUtils.MAC)) {
                path = Paths.get(sdkPath, launcherScriptPath, "language-server-launcher.sh").toString();
            } else if (os.equals(OperatingSystemUtils.WINDOWS)) {
                path = Paths.get(sdkPath, launcherScriptPath, "language-server-launcher.bat").toString();
            }
            if (path != null) {
                setServerDefinition(path);
                LOGGER.info("registered language server definition using Sdk path: " + sdkPath);
                return true;
            }
            return false;
        }
        return false;
    }

    @Nullable
    private static String getBallerinaSdk(Project project) {
        // Todo - Add logic
        return System.getenv("BALLERINA_HOME");
    }

    private static void setServerDefinition(String path) {
        IntellijLanguageClient.addServerDefinition(new RawCommandServerDefinition("bal", new String[] { path }));
        //  IntellijLanguageClient.addExtensionManager("bal", new BallerinaLSPExtensionManager());
    }
}
