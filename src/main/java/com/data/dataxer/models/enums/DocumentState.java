package com.data.dataxer.models.enums;

public class DocumentState {
    public enum InvoiceStates {
        WAITING("waiting"),
        APPROVED("approved"),
        REJECTED("rejected");

        private String stateCode;

        InvoiceStates(String stateCode) {
            this.stateCode = stateCode;
        }

        public static InvoiceStates getStateByCode(String stateCode) {
            return  InvoiceStates.valueOf(stateCode);
        }

        @Override
        public String toString() {
            return this.stateCode;
        }
    }
}
