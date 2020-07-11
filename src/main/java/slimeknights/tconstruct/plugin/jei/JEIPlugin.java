package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.recipe.casting.AbstractCastingRecipe;
import slimeknights.tconstruct.plugin.jei.casting.CastingBasinCategory;
import slimeknights.tconstruct.plugin.jei.casting.CastingTableCategory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
  @Override
  public ResourceLocation getPluginUid() {
    return TConstructRecipeCategoryUid.pluginUid;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(new CastingBasinCategory(guiHelper));
    registry.addRecipeCategories(new CastingTableCategory(guiHelper));
  }

  @Override
  public void registerRecipes(IRecipeRegistration register) {
    List<AbstractCastingRecipe> castingBasinRecipes = RecipeUtil.getRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.CASTING_BASIN, AbstractCastingRecipe.class);
    List<AbstractCastingRecipe> castingTableRecipes = RecipeUtil.getRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.CASTING_TABLE, AbstractCastingRecipe.class);
    register.addRecipes(castingBasinRecipes, TConstructRecipeCategoryUid.castingBasin);
    register.addRecipes(castingTableRecipes, TConstructRecipeCategoryUid.castingTable);
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingBasin), TConstructRecipeCategoryUid.castingBasin);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingTable), TConstructRecipeCategoryUid.castingTable);
  }
}