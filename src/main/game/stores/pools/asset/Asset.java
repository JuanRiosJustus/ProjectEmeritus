package main.game.stores.pools.asset;

import main.game.components.Animation;

public class Asset {
    public static final Asset DEFAULT = new Asset(null, null, null);
    private final String mId;
    private final String mSpriteSheetName;
    private final String mSpriteSheetRowName;
    private final int mColumn;
    private final String mAnimation;
    private final Animation mAnime;
    public Asset(String id, String spriteSheetName, String spriteSheetRowName, int column, String animation, Animation anime) {
        mId = id;
        mSpriteSheetName = spriteSheetName;
        mSpriteSheetRowName = spriteSheetRowName;
        mColumn = column;
        mAnimation = animation;
        mAnime = anime;
    }
    public Asset(String id, String effect,  Animation animation) {
        this(id, null, null, -1, effect, animation);
    }


    public String getId() { return mId; }
    public String getSpriteSheetName() { return mSpriteSheetName; }
    public String getSpriteSheetRowName() { return mSpriteSheetRowName; }
    public int getColumn() { return mColumn; }
    public String getAnimation() { return mAnimation; }
    public Animation getActualAnimation() { return mAnime; }
//    public int getIndex() { return mIndex; }
}
