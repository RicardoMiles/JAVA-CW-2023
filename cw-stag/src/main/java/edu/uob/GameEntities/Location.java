package edu.uob.GameEntities;

import edu.uob.GameEntity;

import java.util.ArrayList;
import java.util.List;

public class Location extends GameEntity {
    private String name;
    private String description;
    private List<Artefacts> artefacts; // 地点中的物品列表
    private List<Furniture> furniture; // 地点中的家具列表
    private List<Characters> characters; // 地点中的角色列表
    private List<Path> paths; // 地点可以通往的路径列表

    private List<Player> players; // Players in specific location
    public Location(String name, String description) {
        super(name, description);
        this.artefacts = new ArrayList<>();
        this.furniture = new ArrayList<>();
        this.characters = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public void addItem(Artefacts item) {
        artefacts.add(item);
    }
    public void removeItem(Artefacts item) {
        artefacts.remove(item);
    }

    // 添加家具
    public void addFurniture(Furniture furniture) {
        this.furniture.add(furniture);
    }

    // 移除家具
    public void removeFurniture(Furniture furniture) {
        this.furniture.remove(furniture);
    }

    // 添加角色
    public void addCharacter(Characters gameCharacter) {
        this.characters.add(gameCharacter);
    }

    // 移除角色
    public void removeCharacter(Character gameCharacter) {
        this.characters.remove(gameCharacter);
    }

    // 添加路径
    public void addPath(String toLocation) {
        Path newPath = new Path(this.name, toLocation);
        this.paths.add(newPath);
    }

    // 移除路径
    public void removePath(Path path) {
        this.paths.remove(path);
    }

    // 获取地点中的物品列表
    public List<Artefacts> getArtefactsItems() {
        return artefacts;
    }

    // 获取地点中的家具列表
    public List<Furniture> getFurniture() {
        return furniture;
    }

    // 获取地点中的角色列表
    public List<Characters> getCharacters() {
        return characters;
    }

    // 获取地点可以通往的路径列表
    public List<Path> getPaths() {
        return paths;
    }


}
