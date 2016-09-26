package de.intektor.pixelshooter.abstrct;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.net.URL;

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
            border_texture_wooden,
            border_breakable_wooden,
            mine_bullet,
            world_type_wooden,
            bronze_medal,
            silver_medal,
            gold_medal,
            dark_heart,
            copy_image,
            empty_stars,
            full_stars,
            green_check_mark,
            main_menu_wooden;

    public static void init() {
        URL resource = ImageStorage.class.getResource("ImageStorage.class");
        boolean inJar = resource != null && resource.toString().startsWith("jar");
        String domain = Gdx.app.getType() == Application.ApplicationType.Desktop && !inJar ? "assets/" : "";
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
            border_texture_wooden = new Texture(domain + "world/wooden/Border_Wooden.png");
            border_breakable_wooden = new Texture(domain + "world/wooden/Border_Breakable_Wooden.png");
            mine_bullet = new Texture(domain + "level_editor/bullet_type/Mine_Bullet.png");
            heavy_ammo = new Texture(domain + "level_editor/bullet_type/Heavy_Ammo.png");
            world_type_wooden = new Texture(domain + "world/wooden/world_type_wooden.png");
            dark_heart = new Texture(domain + "play_state/dark_heart.png");
            bronze_medal = new Texture(domain + "play_state/bronze_medal.png");
            silver_medal = new Texture(domain + "play_state/silver_medal.png");
            gold_medal = new Texture(domain + "play_state/gold_medal.png");
            copy_image = new Texture(domain + "level_editor/tools/copy_tool.png");
            empty_stars = new Texture(domain + "play_state/empty_stars.png");
            full_stars = new Texture(domain + "play_state/full_stars.png");
            main_menu_wooden = new Texture(domain + "menu/main_menu_picture_wooden.png");
            green_check_mark = new Texture(domain + "level_editor/tools/green_check_mark.png");
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
