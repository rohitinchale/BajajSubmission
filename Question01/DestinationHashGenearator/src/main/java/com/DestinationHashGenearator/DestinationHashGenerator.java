package com.DestinationHashGenearator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <JSON File Path>");
            return;
        }

        String prn = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            // Parse the JSON file
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));

            // Traverse the JSON to find the first instance of "destination"
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate a random 8-character alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate PRN, destination value, and random string
            String concatenatedString = prn + destinationValue + randomString;

            // Generate MD5 hash
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result in the format <hash>;<random string>
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                }
                String result = findDestinationValue(field.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                String result = findDestinationValue(item);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}


//N:\>java -jar DestinationHashGeneratorJar.jar 240340120146 N:\test.json
//6b6c71e435c46446f1ce1c98f1188d3e;vZG99D06
