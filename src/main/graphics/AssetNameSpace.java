package main.graphics;

import main.logging.EmeritusLogger;


import java.io.File;
import java.nio.file.FileSystems;
import java.util.*;

public class AssetNameSpace {
    private final Map<String, SpriteSheet> mSpriteSheetMap = new LinkedHashMap<>();
    private final Map<String, SpriteSheet> mShortNameSpriteSheets = new HashMap<>();
    private final static EmeritusLogger logger = EmeritusLogger.create(AssetNameSpace.class);

    public AssetNameSpace(String path, int spriteWidths, int spriteHeights) {
        load(path, spriteWidths, spriteHeights);
    }

    private void load(String directory, int spriteWidths, int spriteHeights) {
        try {
            // Load all content from given path
            logger.info("Started loading Sprite's from {}", directory);
            File content = new File(directory);
            Queue<File> fileQueue = new LinkedList<>();
            Queue<File> spriteFiles = new LinkedList<>();
            Set<String> visitedFiles = new HashSet<>();
            fileQueue.add(content);
            while (!fileQueue.isEmpty()) {
                File currentFile = fileQueue.poll();
                String currentFileName = currentFile.getPath();

                // If visited, don't traverse again (Potentially from symbolic linking
                if (!visitedFiles.add(currentFileName)) { continue; }

                boolean isPNG = currentFileName.endsWith(".png");
                boolean isJPG = currentFileName.endsWith(".jpg");
                boolean isJPEG = currentFileName.endsWith(".jpeg");
                boolean isAnImage = (isPNG || isJPG || isJPEG);

                if (currentFile.isFile() && isAnImage) {
                    spriteFiles.add(currentFile);
                } else if (currentFile.isDirectory()) {
                    File[] files = currentFile.listFiles();
                    if (files == null) { continue; }
                    fileQueue.addAll(Arrays.asList(files));
                }
            }
            logger.info("Finished reading files for sprites");
            while (!spriteFiles.isEmpty()) {
                File currentFile = spriteFiles.poll();

                String path = currentFile.getPath();
                String name = currentFile.getName().substring(0, currentFile.getName().lastIndexOf("."));

                SpriteSheet spriteSheet = new SpriteSheet(path, spriteWidths, spriteHeights);

                mSpriteSheetMap.put(path.toLowerCase(Locale.ROOT), spriteSheet);
                mSpriteSheetMap.put(name.toLowerCase(Locale.ROOT), spriteSheet);
                mShortNameSpriteSheets.put(name.toLowerCase(Locale.ROOT), spriteSheet);
            }
            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
        }
    }


    public SpriteSheet getAsset(String name) {
        return mSpriteSheetMap.get(name.toLowerCase(Locale.ROOT));
    }

    public List<String> getAssetBucket(String bucketName) {
//        Map<String, String> assetBucket = new HashMap<>();
        List<String> sprites = new ArrayList<>();
        for (Map.Entry<String, SpriteSheet> entry : mSpriteSheetMap.entrySet()) {
            String[] partitions = entry.getKey().split(FileSystems.getDefault().getSeparator());
            String fullAssetName = entry.getKey();
            if (partitions.length < 3) { continue; }
            // Third from last is the directory this sprite map was loaded
            String spriteMapName = partitions[partitions.length - 3];
            // Second from last is bucket name
            String bucket = partitions[partitions.length - 2];
            // Last partition is the file name and extension
            String assetNameWithExtension = partitions[partitions.length - 1];

            String assetNameWithNoExtension = assetNameWithExtension.substring(0, assetNameWithExtension.indexOf('.'));

            if (!bucket.equalsIgnoreCase(bucketName)) { continue; }

//            assetBucket.put(assetNameWithNoExtension, fullAssetName);
            sprites.add(fullAssetName);
        }
        return sprites;
    }

//    public List<String[]> getAssetBucket(String bucketName) {
//        List<String[]> sprites = new ArrayList<>();
//        for (Map.Entry<String, Sprite> entry : mSpriteMap.entrySet()) {
//            String[] partitions = entry.getKey().split(FileSystems.getDefault().getSeparator());
//            String fileNameComplete = entry.getKey();
//            if (partitions.length < 3) { continue; }
//            // Third from last is the directory this sprite map was loaded
//            String spriteMapName = partitions[partitions.length - 3];
//            // Second from last is bucket name
//            String bucket = partitions[partitions.length - 2];
//            // Last partition is the file name and extension
//            String fileName = partitions[partitions.length - 1];
//
//            String fileNameNoExtension = fileName.substring(0, fileName.indexOf('.'));
//
//            if (!bucket.equalsIgnoreCase(bucketName)) { continue; }
//
//            sprites.add(new String[]{bucket, fileNameNoExtension, fileNameComplete});
//        }
//        return sprites;
//    }
}
