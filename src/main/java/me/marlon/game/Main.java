package me.marlon.game;

import me.marlon.ecs.*;
import me.marlon.gfx.*;
import me.marlon.physics.PhysicsMaterial;
import me.marlon.physics.RigidBody;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine(1280, 720, "Kinney", 1.0f / 60.0f);
        engine.getWindow().setMouseGrabbed(true);

        BlockSystem blocks = engine.getBlockSystem();
        PhysicsSystem physics = engine.getPhysicsSystem();

        EntityManager entities = engine.getEntities();
        ItemManager items = engine.getItems();
        items.add(new Item("Item A"));
        items.add(new Item("Item B"));

        items.add(new ItemBlock("Test Block", entities, blocks, physics, "res/meshes/box.obj", new BlockFactory() {
            public Block create(Item item, int x, int y, int z) {
                return new Block(item, false, x, y, z);
            }
        }));

        items.add(new ItemBlock("Chest", entities, blocks, physics, "res/meshes/chest.obj", new BlockFactory() {
            public Block create(Item item, int x, int y, int z) {
                return new BlockChest(item, x, y, z, entities, engine.getGui());
            }
        }));

        int terrainEntity = entities.create();
        Terrain terrain = entities.add(terrainEntity, new Terrain(400));
        entities.add(terrainEntity, RigidBody.createTerrain(terrain));
        entities.add(terrainEntity, new TransformComponent());

        int playerEntity = entities.create();
        entities.add(playerEntity, new Camera((float) Math.toRadians(55.0f), 16.0f / 9.0f, 0.2f, 120.0f));

        Player player = new Player(4.0f, 4.0f);
        player.inventory.add(items.get("Item A"), 420);
        player.inventory.add(items.get("Item A"), 69);
        player.inventory.add(items.get("Item B"), 21);
        player.inventory.add(items.get("Test Block"), 64);
        player.inventory.add(items.get("Chest"), 64);

        entities.add(playerEntity, player);

        Vector3f playerPos = new Vector3f(200.0f, 0.0f, 200.0f);
        playerPos.y = terrain.sample(playerPos.x, playerPos.z) + 2.0f;

        RigidBody playerBody = RigidBody.createCuboid(PhysicsMaterial.PLAYER, new Vector3f(0.25f, 1.0f, 0.25f), 1.0f / 50.0f, playerPos);
        playerBody.getInvInertiaTensor().zero();
        playerBody.getAcceleration().y = -10.0f;

        entities.add(playerEntity, playerBody);
        entities.add(playerEntity, new TransformComponent());

        int water = entities.create();
        entities.add(water, new WaterMesh(1024));
        entities.add(water, new TransformComponent().translate(new Vector3f(-512.0f, 4.0f, -512.0f)));

        int sun = entities.create();
        entities.add(sun, new DirectionalLight(new Vector3f(1.0f), new Vector3f(1.25f, -1.5f, 1.0f).normalize()));

        engine.run();
        engine.close();
    }
}
