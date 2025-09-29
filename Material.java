// Material.java
public class Material {
    private int id;
    private String name;
    private float cost;
    private float availableQuantity;
    private String unitOfMeasure;
    private float neededQuantity;

    public Material(int id, String name, float cost, float availableQuantity, String unitOfMeasure) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.availableQuantity = availableQuantity;
        this.unitOfMeasure = unitOfMeasure;
        this.neededQuantity = 0.0f;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getCost() {
        return cost;
    }

    public float getAvailableQuantity() {
        return availableQuantity;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public float getNeededQuantity() {
        return neededQuantity;
    }

    public void setNeededQuantity(float neededQuantity) {
        this.neededQuantity = neededQuantity;
    }

    @Override
    public String toString() {
        return name;
    }
}
