/*
 * Copyright 2018 Karl Kauc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage

@Log4j2
@CompileStatic
/**
 * Start Applicaiton (GUI)
 */
class StartApp extends Application {

    static void main(String... args) {
        launch(StartApp.class, args)
    }

    @Override
    void start(Stage stage) throws Exception {
        log.debug "Starte App"

        log.debug "file.separator: [" + System.getProperty("file.separator") + "]"
        log.debug "java.class.path: [" + System.getProperty("java.class.path") + "]"
        log.debug "java.home: [" + System.getProperty("java.home") + "]"
        log.debug "java.vendor: [" + System.getProperty("java.vendor") + "]"
        log.debug "java.vendor.url: [" + System.getProperty("java.vendor.url") + "]"
        log.debug "java.version: [" + System.getProperty("java.version") + "]"
        log.debug "line.separator: [" + System.getProperty("line.separator").bytes.encodeHex() + "]"
        log.debug "os.arch: [" + System.getProperty("os.arch") + "]"
        log.debug "os.name: [" + System.getProperty("os.name") + "]"
        log.debug "os.version: [" + System.getProperty("os.version") + "]"
        log.debug "path.separator: [" + System.getProperty("path.separator") + "]"
        log.debug "user.dir: [" + System.getProperty("user.dir") + "]"
        log.debug "user.dir: [" + System.getProperty("user.dir") + "]"
        log.debug "user.home: [" + System.getProperty("user.home") + "]"
        log.debug "user.name: [" + System.getProperty("user.name") + "]"
        log.debug "java.specification.version: [" + System.getProperty("java.specification.version", "99.0") + "]"

        Parent root = FXMLLoader.load(getClass().getResource("pages/pageMain.fxml"))
        Scene scene = new Scene(root)

        // Font.loadFont("file:resources/fonts/isadoracyr.ttf", 120)
        Image icon = new Image(getClass().getResourceAsStream("img/connectdevelop.png"))
        stage.getIcons().add(icon)
        stage.setTitle("OeKB Visual Client")
        stage.setScene(scene)
        // stage.setFullScreen(true)

        stage.show()
    }

    @Override
    void stop() throws Exception {
        super.stop()

        log.debug "räume alles auf"
    }
}
