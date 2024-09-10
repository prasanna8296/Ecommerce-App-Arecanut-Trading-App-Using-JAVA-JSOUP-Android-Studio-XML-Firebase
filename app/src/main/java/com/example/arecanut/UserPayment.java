package com.example.arecanut;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

    public class UserPayment implements Serializable {
        private String card;
        private String date;

        public String getCard() {
            return card;
        }

        public void setCard(String card) {
            this.card = card;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getCvv() {
            return cvv;
        }

        public void setCvv(String cvv) {
            this.cvv = cvv;
        }

        public UserPayment(String card, String date, String cvv) {
            this.card = card;
            this.date = date;
            this.cvv = cvv;
        }

        private String cvv;

}
