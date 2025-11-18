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

import java.time.LocalDateTime;

/**
 * Model class for Journal entries
 */
public class JournalEntry {
    public enum ActionType {
        UL("Upload"),
        DL("Download");

        private final String description;

        ActionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum JournalType {
        AR("AccessRules"),
        AR_RECEIVED("Permissions received"),
        AR_ASSIGNED("Permissions assigned"),
        FXML_DATA("FundsXML Data"),
        FXML_REG("FundsXML Regulatory Reporting"),
        FXML_DOC("FundsXML Documents"),
        DATEN_VORH("Download available data"),
        AUSW_DOWNLOAD("Download information on own data downloaded by others"),
        OENB_AGG("OeNB OFI Reporting (Aggregierung)"),
        OENB_SEC("OeNB Sec-by-Sec-Reporting"),
        OENB_CHECK("OeNB Aggregation-Check"),
        JOURNAL("Journal Entries");

        private final String description;

        JournalType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private LocalDateTime timestamp;
    private ActionType action;
    private JournalType type;
    private String userName;
    private String dataSupplier;
    private String uniqueId;
    private String details;
    private boolean isEmpty;

    public JournalEntry() {
    }

    public JournalEntry(LocalDateTime timestamp, ActionType action, JournalType type) {
        this.timestamp = timestamp;
        this.action = action;
        this.type = type;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public JournalType getType() {
        return type;
    }

    public void setType(JournalType type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDataSupplier() {
        return dataSupplier;
    }

    public void setDataSupplier(String dataSupplier) {
        this.dataSupplier = dataSupplier;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
