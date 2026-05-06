package library.model;

public class Member {
    private int memberId;
    private String memberName;
    private String email;
    private String membershipType;

    public Member(String memberName, String email, String membershipType) {
        this.memberName = memberName;
        this.email = email;
        this.membershipType = membershipType;
    }

    public Member(int memberId, String memberName, String email, String membershipType) {
        this.memberId = memberId;
        this.memberName = memberName;
        this.email = email;
        this.membershipType = membershipType;
    }

    public int getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public String getEmail() { return email; }
    public String getMembershipType() { return membershipType; }

    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setMemberName(String memberName) { this.memberName = memberName; }
    public void setEmail(String email) { this.email = email; }
    public void setMembershipType(String type) { this.membershipType = type; }

    @Override
    public String toString() {
        return String.format("%-5d | %-25s | %-35s | %s",
                memberId, memberName, email, membershipType);
    }
}