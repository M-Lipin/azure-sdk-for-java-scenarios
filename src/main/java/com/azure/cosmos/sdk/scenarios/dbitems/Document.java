package com.azure.cosmos.sdk.scenarios.dbitems;

import java.util.UUID;

public class Document {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Document(){
        this.id = UUID.randomUUID().toString();
        this.documentType = UUID.randomUUID().toString();
    }

    public Document(String id, String documentName, String documentCreator, String documentType, DocumentFile documentFile) {
        this.id = id;
        this.documentName = documentName;
        this.documentCreator = documentCreator;
        this.documentType = documentType;
        this.documentFile = documentFile;
    }

    public Document(Document document) {
        this.id = document.id;
        this.documentName = document.documentName;
        this.documentCreator = document.documentCreator;
        this.documentType = document.documentType;
        this.documentFile = document.documentFile;
    }

    // TODO: Add all fields
    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("Document Id: %s\n", id));
        stringBuilder.append(String.format("Document Type: %s\n", documentType));

        return stringBuilder.toString();
    }

    private String documentName;

    private String documentCreator;

    private String documentType;

    private DocumentFile documentFile;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentCreator() {
        return documentCreator;
    }

    public void setDocumentCreator(String documentCreator) {
        this.documentCreator = documentCreator;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public DocumentFile getDocumentFile() {
        return documentFile;
    }

    public void setDocumentFile(DocumentFile documentFile) {
        this.documentFile = documentFile;
    }
}
