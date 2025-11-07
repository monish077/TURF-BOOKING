// package com.example.demo.turfbooking.debug;

// import com.example.demo.turfbooking.entity.User;
// import com.example.demo.turfbooking.repository.UserRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// @Component
// public class MongoDebug implements CommandLineRunner {

//     @Autowired
//     private UserRepository userRepository;

//     @Override
//     public void run(String... args) throws Exception {
//         System.out.println("🔍 === MONGODB DEBUG START ===");
        
//         // Check current count
//         long count = userRepository.count();
//         System.out.println("📊 Current users in database: " + count);
        
//         // Create a test user with minimal fields
//         User testUser = new User();
//         testUser.setEmail("debug@example.com");
//         testUser.setPassword("debug123");
//         testUser.setEnabled(true);
        
//         // Try to set name without causing errors
//         try {
//             testUser.getClass().getMethod("setFirstname", String.class).invoke(testUser, "Debug");
//             testUser.getClass().getMethod("setLastname", String.class).invoke(testUser, "Test");
//         } catch (Exception e) {
//             // If those methods don't exist, try camelCase
//             try {
//                 testUser.getClass().getMethod("setFirstName", String.class).invoke(testUser, "Debug");
//                 testUser.getClass().getMethod("setLastName", String.class).invoke(testUser, "Test");
//             } catch (Exception e2) {
//                 // If still fails, just continue without names
//                 System.out.println("⚠️  Name fields not available, continuing without them");
//             }
//         }
        
//         try {
//             User saved = userRepository.save(testUser);
//             System.out.println("✅ Test user saved with ID: " + saved.getId());
//             System.out.println("📊 Users after save: " + userRepository.count());
//         } catch (Exception e) {
//             System.out.println("❌ Error saving user: " + e.getMessage());
//             e.printStackTrace();
//         }
        
//         System.out.println("🔍 === MONGODB DEBUG END ===");
//     }
// }