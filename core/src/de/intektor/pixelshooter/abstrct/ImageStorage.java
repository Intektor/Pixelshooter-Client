package de.intektor.pixelshooter.abstrct;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

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
            triangleImage,
            bronze_medal,
            silver_medal,
            gold_medal,
            dark_heart,
            copy_image,
            empty_stars,
            full_stars;

    public static void init() {
        String domain = Gdx.app.getType() == Application.ApplicationType.Desktop ? "assets/" : "";

        try {
            tankExplosion = new Texture(domain + "TankExplosion.png");
            pointer = new Texture(domain + "Select.png");
            collisionImage = new Texture(domain + "Collision.png");
            trashCan = new Texture(domain + "TrashCan.png");
            standard_ammo = new Texture(domain + "Standard_Ammo.png");
            heart = new Texture(domain + "heart.png");
            mag_glass = new Texture(domain + "Magnifying glass.png");
            triple_ammo = new Texture(domain + "Triple_Ammo.png");
            chasing_ammo = new Texture(domain + "Chasing_Ammo.png");
            grab_cursor = new Texture(domain + "Grab.png");
            background_wooden = new Texture(domain + "Background_wooden.png");
            background_grass = new Texture(domain + "Background_grass.png");
            border_texture_wooden = new Texture(domain + "Border_Wooden.png");
            border_texture_grass = new Texture(domain + "Border_Grass.png");
            border_breakable_wooden = new Texture(domain + "Border_Breakable_Wooden.png");
            border_breakable_grass = new Texture(domain + "Border_Breakable_Grass.png");
            mine_bullet = new Texture(domain + "Mine_Bullet.png");
            triangleImage = new Texture(domain + "Triangle.png");
            heavy_ammo = new Texture(domain + "Heavy_Ammo.png");
            world_type_wooden = new Texture(domain + "world_type_wooden.png");
            world_type_grass = new Texture(domain + "world_type_grass.png");
            dark_heart = new Texture(domain + "dark_heart.png");
            bronze_medal = new Texture(domain + "bronze_medal.png");
            silver_medal = new Texture(domain + "silver_medal.png");
            gold_medal = new Texture(domain + "gold_medal.png");
            copy_image = new Texture(domain + "copy_tool.png");
            empty_stars = new Texture(domain + "empty_stars.png");
            full_stars = new Texture(domain + "full_stars.png");
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
        triangleImage.dispose();
        heavy_ammo.dispose();
        copy_image.dispose();
    }
}
