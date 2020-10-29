package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "WeakerAccess"})
@RequiredArgsConstructor(staticName = "castingRecipe")
public class MaterialCastingRecipeBuilder extends AbstractRecipeBuilder<MaterialCastingRecipeBuilder> {
  private final IMaterialItem result;
  private final MaterialCastingRecipeSerializer<?> recipeSerializer;
  private Ingredient cast = Ingredient.EMPTY;
  @Setter @Accessors(chain = true)
  private int fluidAmount = 0;
  private boolean consumed = false;
  private boolean switchSlots = false;

  /**
   * Creates a new material casting recipe for an basin recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder basinRecipe(IMaterialItem result) {
    return castingRecipe(result, TinkerSmeltery.basinMaterialSerializer.get());
  }

  /**
   * Creates a new material casting recipe for an table recipe
   * @param result            Material item result
   * @return  Builder instance
   */
  public static MaterialCastingRecipeBuilder tableRecipe(IMaterialItem result) {
    return castingRecipe(result, TinkerSmeltery.tableMaterialSerializer.get());
  }

  /**
   * Sets the cast to the given tag
   * @param tag       Cast tag
   * @param consumed  If true, cast is consumed
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(Tag<Item> tag, boolean consumed) {
    return this.setCast(Ingredient.fromTag(tag), consumed);
  }

  /**
   * Sets the cast to the given item
   * @param item      Cast item
   * @param consumed  If true, cast is consumed
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(IItemProvider item, boolean consumed) {
    return this.setCast(Ingredient.fromItems(item), consumed);
  }

  /**
   * Set the cast to the given ingredient
   * @param cast      Ingredient
   * @param consumed  If true, cast is consumed
   * @return  Builder instance
   */
  public MaterialCastingRecipeBuilder setCast(Ingredient cast, boolean consumed) {
    this.cast = cast;
    this.consumed = consumed;
    return this;
  }

  /**
   * Set output of recipe to be put into the input slot.
   * Mostly used for cast creation
   */
  public MaterialCastingRecipeBuilder setSwitchSlots() {
    this.switchSlots = true;
    return this;
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    this.build(consumer, Objects.requireNonNull(this.result.asItem().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    if (this.fluidAmount <= 0) {
      throw new IllegalStateException("Material casting recipes require a positive amount of fluid");
    }
    ResourceLocation advancementId = this.buildAdvancement(id, "casting");
    consumer.accept(new Result(id, this.group, this.consumed, this.switchSlots, this.fluidAmount, this.cast, this.result, this.advancementBuilder, advancementId, this.recipeSerializer));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    protected final ResourceLocation ID;
    private final String group;
    private final boolean consumed;
    private final boolean switchSlots;
    private final int fluidAmount;
    private final Ingredient cast;
    private final IMaterialItem result;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;
    @Getter
    private final IRecipeSerializer<? extends MaterialCastingRecipe> serializer;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      if (cast != Ingredient.EMPTY) {
        json.add("cast", this.cast.serialize());
        if (this.consumed) {
          json.addProperty("cast_consumed", true);
        }
      }
      if (this.switchSlots) {
        json.addProperty("switch_slots", true);
      }
      json.addProperty("fluid_amount", this.fluidAmount);
      json.addProperty("result", Objects.requireNonNull(this.result.asItem().getRegistryName()).toString());
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}