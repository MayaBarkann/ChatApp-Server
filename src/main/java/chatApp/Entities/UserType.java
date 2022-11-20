package chatApp.Entities;

public enum UserType {
    ADMIN ("admin"),
    GUEST ("guest"),
    REGISTERED ("registered"),
    NOT_ACTIVATED ("not_activated");

    private final String typeName;

    private UserType(String typeName){
        this.typeName=typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }

    public boolean equalsTypeName(String otherTypeName){
        return this.typeName.equals(otherTypeName);
    }
}
