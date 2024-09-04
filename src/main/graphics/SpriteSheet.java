package main.graphics;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.*;

public class SpriteSheet {
    private final Map<String, Sprite> mSpriteMap = new LinkedHashMap<>();
    private final Map<String, Sprite> mLoadedSpriteMap = new LinkedHashMap<>();
    private final Map<String, String> mSpriteToLoadReference = new LinkedHashMap<>();
    private String mSpriteSheetRoot = null;
    private int mSpriteWidths = 0;
    private int mSpriteHeights = 0;
    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteSheetOG.class);

    public SpriteSheet(String path, int spriteWidths, int spriteHeights) {
        mSpriteSheetRoot = path;
        mSpriteWidths = spriteWidths;
        mSpriteHeights = spriteHeights;
        load(path, spriteWidths, spriteHeights);
    }

    private void load(String directory, int spriteWidths, int spriteHeights) {
        try {
            // Load all content from given path
            logger.info("Started loading Sprite's from {}", directory);
            mSpriteSheetRoot = directory;
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

                Sprite sprite = new Sprite(path, spriteWidths, spriteHeights);
//                mSpriteToLoadReference.put(path, path);
//                mSpriteToLoadReference.put(name, path);

                mSpriteMap.put(path, sprite);
                mSpriteMap.put(name, sprite);
            }
            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
        }
    }

    public Sprite getSprite(String name) {
//        String reference = mSpriteToLoadReference.get(name);
//        Sprite sprite = mLoadedSpriteMap.get(reference);
//        if (sprite == null) {
//            sprite = new Sprite(reference, mSpriteWidths, mSpriteHeights);
//            mLoadedSpriteMap.put(reference, sprite);
//        }
//
//        return sprite;
        return mSpriteMap.get(name);
    }

    public List<String> getBucket(String spriteBucket) {
        List<String> sprites = new ArrayList<>();
        for (Map.Entry<String, Sprite> entry : mSpriteMap.entrySet()) {
            String[] partitions = entry.getKey().split(FileSystems.getDefault().getSeparator());
            if (partitions.length < 3) { continue; }
            // Third from last is the directory this sprite map was loaded
            String spriteMapName = partitions[partitions.length - 3];
            // Second from last is bucket name
            String bucketName = partitions[partitions.length - 2];
            // Last partition is the file name and extension
            String fileName = partitions[partitions.length - 1];

            if (!bucketName.equalsIgnoreCase(spriteBucket)) { continue; }
            sprites.add(entry.getKey());
        }
        return sprites;
    }
}
