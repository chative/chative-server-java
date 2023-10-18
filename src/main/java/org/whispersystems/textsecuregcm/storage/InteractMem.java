package org.whispersystems.textsecuregcm.storage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;
import java.util.Objects;

public class InteractMem {
    @JsonProperty
    long thumbsUpCount;
    @JsonProperty
    LinkedList<InteractMemSource> lastSource;
    public InteractMem(){
    }
    public InteractMem(long thumbsUpCount,LinkedList<InteractMemSource> lastSource){
        this.thumbsUpCount=thumbsUpCount;
        this.lastSource=lastSource;
    }

    public long getThumbsUpCount() {
        return thumbsUpCount;
    }

    public void setThumbsUpCount(long thumbsUpCount) {
        this.thumbsUpCount = thumbsUpCount;
    }

    public void addSource(InteractMemSource interactMemSource) {
        if(this.lastSource.getFirst().equals(interactMemSource)){
            return;
        }
        if(this.lastSource.contains(interactMemSource)){
            this.lastSource.remove(interactMemSource);
        }
        this.lastSource.addFirst(interactMemSource);
        while (lastSource.size()>InteractsTable.LIMIT){
            lastSource.removeLast();
        }
    }

    public LinkedList<InteractMemSource> getLastSource() {
        return lastSource;
    }

    public void setLastSource(LinkedList<InteractMemSource> lastSource) {
        this.lastSource = lastSource;
    }

    public static class InteractMemSource {
        @JsonProperty
        private String number;
        @JsonProperty
        private String publicName;

        public InteractMemSource() {
        }
        public InteractMemSource(String number,String publicName) {
            this.number=number;
            this.publicName=publicName;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getPublicName() {
            return publicName;
        }

        public void setPublicName(String publicName) {
            this.publicName = publicName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InteractMemSource that = (InteractMemSource) o;
            return Objects.equals(number, that.number) && Objects.equals(publicName, that.publicName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(number, publicName);
        }

    }
}
