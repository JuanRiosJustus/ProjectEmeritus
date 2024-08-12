package main.graphics;

import main.logging.ELogger;
import main.logging.ELoggerFactory;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.*;

public class SpriteSheetV2 {
    private final Map<String, Sprite> mSpriteMap = new LinkedHashMap<>();
    private String mSpriteMapPath = null;
    private final static ELogger logger = ELoggerFactory.getInstance().getELogger(SpriteSheet.class);

    public SpriteSheetV2(String path, int spriteWidths, int spriteHeights) {
        load(path, spriteWidths, spriteHeights);
    }

    private void load(String directory, int spriteWidths, int spriteHeights) {
        try {
            // Load all content from given path
            logger.info("Started loading Sprite's from {}", directory);
            mSpriteMapPath = directory;
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
                Sprite sprite = new Sprite(path, spriteWidths, spriteHeights);
                mSpriteMap.put(path, sprite);
                mSpriteMap.put(currentFile.getName().substring(0, currentFile.getName().lastIndexOf(".")), sprite);
            }
            logger.info("Finished loading {}", directory);
        } catch (Exception e) {
            logger.error("Failed loading {} because {}", directory, e);
        }
    }

    public Sprite getSprite(String name) {
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
