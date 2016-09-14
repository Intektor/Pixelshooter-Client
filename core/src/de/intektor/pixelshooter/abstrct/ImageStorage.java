package de.intektor.pixelshooter.abstrct;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import static com.badlogic.gdx.Application.ApplicationType.Desktop;

/**
 * @author Intektor
 */
public class ImageStorage {

    public static Texture tankExplosion,
            pointer,
            collisionImage,
            trashCan,
            standard_ammo,
            heart, mag_glass,
            triple_ammo,
            chasing_ammo,
            heavy_ammo,
            grab_cursor,
            background_wooden,
            background_grass,
            border_texture_wooden,
            border_texture_grass,
            border_breakable_wooden,
            border_breakable_grass,
            mine_bullet,
            world_type_wooden,
            world_type_grass,
            bronze_medal,
            silver_medal,
            gold_medal,
            dark_heart,
            copy_image,
            empty_stars,
            full_stars,

            background_desert,
            border_desert_breakable,
            border_desert_unbreakable,
            tiny_picture_desert;

    public static void init() {
        String domain = Gdx.app.getType() == Desktop ? "assets/" : "";

        try {
            tankExplosion = new Texture(domain + "level_editor/bullet_type/TankExplosion.png");
            pointer = new Texture(domain + "level_editor/tools/Select.png");
            collisionImage = new Texture(domain + "level_editor/tools/Collision.png");
            trashCan = new Texture(domain + "level_editor/tools/TrashCan.png");
            standard_ammo = new Texture(domain + "level_editor/bullet_type/Standard_Ammo.png");
            heart = new Texture(domain + "play_state/heart.png");
            mag_glass = new Texture(domain + "level_editor/tools/Magnifying glass.png");
            triple_ammo = new Texture(domain + "level_editor/bullet_type/Triple_Ammo.png");
            chasing_ammo = new Texture(domain + "level_editor/bullet_type/Chasing_Ammo.png");
            grab_cursor = new Texture(domain + "level_editor/tools/Grab.png");
            background_wooden = new Texture(domain + "world/wooden/Background_wooden.png");
            background_grass = new Texture(domain + "world/grass/Background_grass.png");
            border_texture_wooden = new Texture(domain + "world/wooden/Border_Wooden.png");
            border_texture_grass = new Texture(domain + "world/grass/Border_Grass.png");
            border_breakable_wooden = new Texture(domain + "world/wooden/Border_Breakable_Wooden.png");
            border_breakable_grass = new Texture(domain + "world/grass/Border_Breakable_Grass.png");
            mine_bullet = new Texture(domain + "level_editor/bullet_type/Mine_Bullet.png");
            heavy_ammo = new Texture(domain + "level_editor/bullet_type/Heavy_Ammo.png");
            world_type_wooden = new Texture(domain + "world/wooden/world_type_wooden.png");
            world_type_grass = new Texture(domain + "world/grass/world_type_grass.png");
            dark_heart = new Texture(domain + "play_state/dark_heart.png");
            bronze_medal = new Texture(domain + "play_state/bronze_medal.png");
            silver_medal = new Texture(domain + "play_state/silver_medal.png");
            gold_medal = new Texture(domain + "play_state/gold_medal.png");
            copy_image = new Texture(domain + "level_editor/tools/copy_tool.png");
            empty_stars = new Texture(domain + "play_state/empty_stars.png");
            full_stars = new Texture(domain + "play_state/full_stars.png");
            background_desert = new Texture(domain + "world/desert/background_desert.png");
            border_desert_breakable = new Texture(domain + "world/desert/border_desert_breakable.png");
            border_desert_unbreakable = new Texture(domain + "world/desert/border_desert_unbreakable.png");
            tiny_picture_desert = new Texture(domain + "world/desert/desert_tiny_picture.png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void dispose() {
        tankExplosion.dispose();
        pointer.dispose();
        collisionImage.dispose();
        trashCan.dispose();
        standard_ammo.dispose();
        standard_ammo.dispose();
        heart.dispose();
        mag_glass.dispose();
        trashCan.dispose();
        chasing_ammo.dispose();
        grab_cursor.dispose();
        background_wooden.dispose();
        border_texture_wooden.dispose();
        mine_bullet.dispose();
        heavy_ammo.dispose();
        copy_image.dispose();
    }
}
