//package MyClasses.Security;
//
//import javax.crypto.*;
//import javax.crypto.spec.SecretKeySpec;
//import java.io.*;
//import java.nio.file.Files;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//
//
//public class SecurityManager implements Security {
//    private static final String ALGORITHM = "AES";
//    private static final String ENCRYPTED_EXTENSION = ".encrypted";
//    private SecretKey secretKey;
//
//    public SecurityManager(String password) throws Exception {
//        // Generate key from password (simplified - use proper key derivation in production)
//        byte[] key = password.getBytes();
//        byte[] keyBytes = new byte[16]; // AES-128
//        System.arraycopy(key, 0, keyBytes, 0, Math.min(key.length, keyBytes.length));
//        this.secretKey = new SecretKeySpec(keyBytes, ALGORITHM);
//    }
//
//    @Override
//    public File encrypt(File file) {
//        if (file == null || !file.exists()) {
//            throw new IllegalArgumentException("File does not exist");
//        }
//
//        if (isEncrypted(file)) {
//            System.out.println("File is already encrypted: " + file.getName());
//            return file;
//        }
//
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//
//            byte[] fileContent = Files.readAllBytes(file.toPath());
//            byte[] encryptedContent = cipher.doFinal(fileContent);
//
//            File encryptedFile = new File(file.getAbsolutePath() + ENCRYPTED_EXTENSION);
//            try (FileOutputStream fos = new FileOutputStream(encryptedFile)) {
//                fos.write(encryptedContent);
//            }
//
//            System.out.println("File encrypted successfully: " + encryptedFile.getName());
//            return encryptedFile;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public File decrypt(File file) {
//        if (file == null || !file.exists()) {
//            throw new IllegalArgumentException("File does not exist");
//        }
//
//        if (!isEncrypted(file)) {
//            throw new IllegalArgumentException("File is not encrypted: " + file.getName());
//        }
//
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//
//            byte[] encryptedContent = Files.readAllBytes(file.toPath());
//            byte[] decryptedContent = cipher.doFinal(encryptedContent);
//
//            String originalPath = file.getAbsolutePath().replace(ENCRYPTED_EXTENSION, "");
//            File decryptedFile = new File(originalPath);
//
//            try (FileOutputStream fos = new FileOutputStream(decryptedFile)) {
//                fos.write(decryptedContent);
//            }
//
//            System.out.println("File decrypted successfully: " + decryptedFile.getName());
//            return decryptedFile;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public boolean isEncrypted(File file) {
//        return file != null && file.getName().endsWith(ENCRYPTED_EXTENSION);
//    }
//
//    /**
//     * Encrypts formulation data to string (for database storage)
//     */
//    public String encryptString(String data) {
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
//            return Base64.getEncoder().encodeToString(encryptedBytes);
//        } catch (Exception e) {
//            throw new RuntimeException("String encryption failed: " + e.getMessage(), e);
//        }
//    }
//
//    /**
//     * Decrypts formulation data from string
//     */
//    public String decryptString(String encryptedData) {
//        try {
//            Cipher cipher = Cipher.getInstance(ALGORITHM);
//            cipher.init(Cipher.DECRYPT_MODE, secretKey);
//            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
//            return new String(decryptedBytes);
//        } catch (Exception e) {
//            throw new RuntimeException("String decryption failed: " + e.getMessage(), e);
//        }
//    }
//}