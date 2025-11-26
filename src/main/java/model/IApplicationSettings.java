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
package model;

/**
 * Interface for application settings.
 * This interface enables dependency injection and easier unit testing
 * by allowing mock implementations to be injected instead of the singleton.
 */
public interface IApplicationSettings {

    /**
     * Read settings from the configuration file.
     */
    void readSettingsFromFile();

    /**
     * Check if production server should be used.
     * @return true if production server, false for test server
     */
    boolean isUseProdServer();

    /**
     * Check if system proxy settings should be used.
     * @return true if system proxy settings should be used
     */
    boolean isConnectionUseSystemSettings();

    /**
     * Get the proxy host.
     * @return proxy host or null if not configured
     */
    String getConnectionProxyHost();

    /**
     * Get the proxy port.
     * @return proxy port or null if not configured
     */
    Integer getConnectionProxyPort();

    /**
     * Check if file system mode is enabled (for offline testing).
     * @return true if file system mode is enabled
     */
    boolean isFileSystem();

    /**
     * Get the OeKB server URL.
     * @return the server URL
     */
    String getServerURL();

    /**
     * Get the Basic authentication credentials (Base64 encoded).
     * @return Base64 encoded credentials
     */
    String getAuthCredentialsBasic();

    /**
     * Get the OeKB username.
     * @return the username
     */
    String getOekbUserName();

    /**
     * Get the data supplier list.
     * @return the data supplier identifier
     */
    String getDataSupplierList();

    /**
     * Get the backup directory path.
     * @return the backup directory path
     */
    String getBackupDirectory();
}
