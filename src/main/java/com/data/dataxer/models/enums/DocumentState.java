package com.data.dataxer.models.enums;

public enum DocumentState {
        WAITING("waiting"),
        APPROVED("approved"),
        REJECTED("rejected"),
        PAYED("payed");

        private String stateCode;

        DocumentState(String stateCode) {
            this.stateCode = stateCode;
        }

        public static DocumentState getStateByCode(String stateCode) {
            return  DocumentState.valueOf(stateCode);
        }

        public String getStateCode() {
            return this.stateCode;
        }

        @Override
        public String toString() {
            return this.stateCode;
        }
}
