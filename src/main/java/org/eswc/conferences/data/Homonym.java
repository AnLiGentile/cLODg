package org.eswc.conferences.data;

public class Homonym {
    private String email;
    private String id;
    
    public Homonym(String email, String id) {
        this.email = email;
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    
}
