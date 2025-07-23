package com.MSIL.MSIL_Project.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class for generating BCrypt password hashes for Flyway migrations
 *
 * Usage:
 * 1. Run this class as a standalone Java application
 * 2. Copy the generated hash to your Flyway migration script
 * 3. Use the hash in INSERT statements for user passwords
 *
 * Example:
 * java -cp "target/classes:~/.m2/repository/org/springframework/security/spring-security-crypto/6.2.0/spring-security-crypto-6.2.0.jar" com.example.rbac.util.PasswordGeneratorUtil
 */
public class PasswordGeneratorUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * Generate BCrypt hash for a password
     * @param plainPassword The plain text password
     * @return BCrypt hash string
     */
    public static String generateHash(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    /**
     * Verify if a plain password matches a BCrypt hash
     * @param plainPassword The plain text password
     * @param hashedPassword The BCrypt hash
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return encoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Main method for generating password hashes
     * Run this to generate new password hashes for migration scripts
     */
    public static void main(String[] args) {
        System.out.println("🔐 === BCrypt Password Hash Generator for Flyway ===");
        System.out.println();

        // Generate hash for default Super Admin password
        String superAdminPassword = "SuperAdmin@123";
        String hash = generateHash(superAdminPassword);

        System.out.println("📋 Default Super Admin Credentials:");
        System.out.println("   Plain Password: " + superAdminPassword);
        System.out.println("   BCrypt Hash: " + hash);
        System.out.println();

        System.out.println("📝 SQL for Flyway migration (V2__Insert_Initial_Data.sql):");
        System.out.println("INSERT INTO users (email, name, password, user_type, user_group, channel, organization, created_by, must_change_password) VALUES");
        System.out.println("('superadmin@example.com', 'Super Administrator', '" + hash + "', 'INTERNAL', 'ADMIN_GROUP', 'WEB', 'SYSTEM_ORG', 'SYSTEM', false);");
        System.out.println();

        // Verify the hash works
        boolean isValid = verifyPassword(superAdminPassword, hash);
        System.out.println("✅ Hash verification: " + (isValid ? "VALID ✓" : "❌ INVALID ✗"));
        System.out.println();

        // Generate additional sample passwords for different users
        String[] samplePasswords = {"Admin@123", "RSAManager@123", "User@123", "Manager@456"};
        System.out.println("🔐 Additional Sample Password Hashes:");
        System.out.println("   (Use these for creating additional users)");
        for (String password : samplePasswords) {
            String sampleHash = generateHash(password);
            System.out.println(String.format("   %-15s -> %s", password, sampleHash));
        }

        System.out.println();
        System.out.println("💡 Tips:");
        System.out.println("   • Each time you run this, BCrypt generates a different hash for the same password");
        System.out.println("   • This is normal and secure - any of these hashes will work for authentication");
        System.out.println("   • Copy the hash exactly as shown (including $2a$10$...)");
        System.out.println("   • Test the generated hash with your login system before deploying");
    }

    /**
     * Helper method to generate a hash for a specific password
     * Useful for programmatic hash generation
     */
    public static void generateHashForPassword(String password) {
        String hash = generateHash(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
        System.out.println("Verification: " + verifyPassword(password, hash));
    }
}
