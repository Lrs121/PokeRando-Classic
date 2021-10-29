package org.lrsservers.pokerando.upr.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  RomHandler.java - defines the functionality that each randomization   --*/
/*--                    handler must implement.                             --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

//import java.awt.image.BufferedImage;

import android.content.pm.PackageManager;

import org.lrsservers.pokerando.upr.CustomNamesSet;
import org.lrsservers.pokerando.upr.MiscTweak;
import org.lrsservers.pokerando.upr.pokemon.EncounterSet;
import org.lrsservers.pokerando.upr.pokemon.GenRestrictions;
import org.lrsservers.pokerando.upr.pokemon.IngameTrade;
import org.lrsservers.pokerando.upr.pokemon.ItemList;
import org.lrsservers.pokerando.upr.pokemon.Move;
import org.lrsservers.pokerando.upr.pokemon.MoveLearnt;
import org.lrsservers.pokerando.upr.pokemon.Pokemon;
import org.lrsservers.pokerando.upr.pokemon.Trainer;
import org.lrsservers.pokerando.upr.pokemon.Type;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

public interface RomHandler {

    boolean loadRom(String filename);

    // Basic load/save to filenames

    boolean saveRom(String filename);

    String loadedFilename();

    void setLog(PrintStream logStream);

    // Log stuff

    // Get a List of Pokemon objects in this game.
    // 0 = null 1-whatever = the Pokemon.
    List<Pokemon> getPokemon();

    // Setup Gen Restrictions.
    void setPokemonPool(GenRestrictions restrictions);

    void removeEvosForPokemonPool();

    // Randomizer: Starters
    // Get starters, they should be ordered with Pokemon
    // following the one it is SE against.
    // E.g. Grass, Fire, Water or Fire, Water, Grass etc.
    List<Pokemon> getStarters();

    // Change the starter data in the ROM.
    // Optionally also change the starter used by the rival in
    // the level 5 battle, if there is one.
    boolean setStarters(List<Pokemon> newStarters);

    // Tells whether this ROM has the ability to have starters changed.
    // Was for before CUE's compressors were found and arm9 was untouchable.
    boolean canChangeStarters();

    // Run the stats shuffler on each Pokemon.
    void shufflePokemonStats(boolean evolutionSanity);

    // Randomizer: Pokemon stats

    // Randomise stats following evolutions for proportions or not (see
    // tooltips)
    void randomizePokemonStats(boolean evolutionSanity);

    // Update base stats to gen6
    void updatePokemonStats();

    // Give a random Pokemon who's in this game
    Pokemon randomPokemon();

    // Give a random non-legendary Pokemon who's in this game
    // Business rules for who's legendary are in Pokemon class
    Pokemon randomNonLegendaryPokemon();

    // Give a random legendary Pokemon who's in this game
    // Business rules for who's legendary are in Pokemon class
    Pokemon randomLegendaryPokemon();

    // Give a random Pokemon who has 2 evolution stages
    // Should make a good starter Pokemon
    Pokemon random2EvosPokemon();

    // return a random type valid in this game.
    // straightforward except for gen1 where dark&steel are excluded.
    Type randomType();

    // Randomizer: types

    boolean typeInGame(Type type);

    // randomise Pokemon types, with a switch on whether evolutions
    // should follow the same types or not.
    // some evolutions dont anyway, e.g. Eeveelutions, Hitmons
    void randomizePokemonTypes(boolean evolutionSanity);

    // Randomizer: pokemon abilities
    int abilitiesPerPokemon();

    int highestAbilityIndex();

    String abilityName(int number);

    void randomizeAbilities(boolean evolutionSanity, boolean allowWonderGuard, boolean banTrappingAbilities,
                            boolean banNegativeAbilities);

    // Randomizer: wild pokemon
    List<EncounterSet> getEncounters(boolean useTimeOfDay);

    void setEncounters(boolean useTimeOfDay, List<EncounterSet> encounters);

    void randomEncounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed, boolean usePowerLevels,
                          boolean noLegendaries);

    void area1to1Encounters(boolean useTimeOfDay, boolean catchEmAll, boolean typeThemed,
                            boolean usePowerLevels, boolean noLegendaries);

    void game1to1Encounters(boolean useTimeOfDay, boolean usePowerLevels, boolean noLegendaries);

    boolean hasTimeBasedEncounters();

    List<Pokemon> bannedForWildEncounters();

    // Randomizer: trainer pokemon
    List<Trainer> getTrainers();

    void setTrainers(List<Trainer> trainerData);

    void randomizeTrainerPokes(boolean usePowerLevels, boolean noLegendaries, boolean noEarlyWonderGuard,
                               int levelModifier);

    void typeThemeTrainerPokes(boolean usePowerLevels, boolean weightByFrequency, boolean noLegendaries,
                               boolean noEarlyWonderGuard, int levelModifier);

    void rivalCarriesStarter();

    void forceFullyEvolvedTrainerPokes(int minLevel);

    void randomizeMovePowers();

    // Randomizer: moves

    void randomizeMovePPs();

    void randomizeMoveAccuracies();

    void randomizeMoveTypes();

    boolean hasPhysicalSpecialSplit();

    void randomizeMoveCategory();

    // Update all moves to gen5 definitions as much as possible
    // e.g. change typing, power, accuracy, but don't try to
    // stuff around with effects.
    void updateMovesToGen5();

    // same for gen6
    void updateMovesToGen6();

    // stuff for printing move changes
    void initMoveUpdates();

    void printMoveUpdates();

    // return all the moves valid in this game.
    List<Move> getMoves();

    Map<Pokemon, List<MoveLearnt>> getMovesLearnt();

    // Randomizer: moves learnt

    void setMovesLearnt(Map<Pokemon, List<MoveLearnt>> movesets);

    List<Integer> getMovesBannedFromLevelup();

    void randomizeMovesLearnt(boolean typeThemed, boolean noBroken, boolean forceFourStartingMoves,
                              double goodDamagingProbability);

    void orderDamagingMovesByDamage();

    void metronomeOnlyMode();

    boolean supportsFourStartingMoves();

    List<Pokemon> getStaticPokemon();

    // Randomizer: static pokemon (except starters)

    boolean setStaticPokemon(List<Pokemon> staticPokemon);

    void randomizeStaticPokemon(boolean legendForLegend);

    boolean canChangeStaticPokemon();

    List<Pokemon> bannedForStaticPokemon();

    List<Integer> getTMMoves();

    // Randomizer: TMs/HMs

    void setTMMoves(List<Integer> moveIndexes);

    List<Integer> getHMMoves();

    void randomizeTMMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability);

    int getTMCount();

    int getHMCount();

    /**
     * Get TM/HM compatibility data from this rom. The result should contain a
     * boolean array for each Pokemon indexed as such:
     * <p>
     * 0: blank (false) / 1 - (getTMCount()) : TM compatibility /
     * (getTMCount()+1) - (getTMCount()+getHMCount()) - HM compatibility
     *
     * @return
     */

    Map<Pokemon, boolean[]> getTMHMCompatibility();

    void setTMHMCompatibility(Map<Pokemon, boolean[]> compatData);

    void randomizeTMHMCompatibility(boolean preferSameType);

    void fullTMHMCompatibility();

    void ensureTMCompatSanity();

    // tm/moveset sanity

    void fullHMCompatibility();

    // new 170: full HM (but not TM) compat override

    boolean hasMoveTutors();

    // Randomizer: move tutors

    List<Integer> getMoveTutorMoves();

    void setMoveTutorMoves(List<Integer> moves);

    void randomizeMoveTutorMoves(boolean noBroken, boolean preserveField, double goodDamagingProbability);

    Map<Pokemon, boolean[]> getMoveTutorCompatibility();

    void setMoveTutorCompatibility(Map<Pokemon, boolean[]> compatData);

    void randomizeMoveTutorCompatibility(boolean preferSameType);

    void fullMoveTutorCompatibility();

    void ensureMoveTutorCompatSanity();

    // mt/moveset sanity

    boolean canChangeTrainerText();

    // Randomizer: trainer names

    List<String> getTrainerNames();

    void setTrainerNames(List<String> trainerNames);

    TrainerNameMode trainerNameMode();

    // Returns this with or without the class
    int maxTrainerNameLength();

    // Only relevant for gen2, which has fluid trainer name length but
    // only a certain amount of space in the ROM bank.
    int maxSumOfTrainerNameLengths();

    // Only needed if above mode is "MAX LENGTH WITH CLASS"
    List<Integer> getTCNameLengthsByTrainer();

    void randomizeTrainerNames(CustomNamesSet customNames);

    List<String> getTrainerClassNames();

    void setTrainerClassNames(List<String> trainerClassNames);

    // Randomizer: trainer class names

    boolean fixedTrainerClassNamesLength();

    int maxTrainerClassNameLength();

    void randomizeTrainerClassNames(CustomNamesSet customNames);

    List<Integer> getDoublesTrainerClasses();

    ItemList getAllowedItems();

    ItemList getNonBadItems();

    // Items

    void randomizeWildHeldItems(boolean banBadItems);

    String[] getItemNames();

    List<Integer> getStarterHeldItems();

    void setStarterHeldItems(List<Integer> items);

    void randomizeStarterHeldItems(boolean banBadItems);

    List<Integer> getRequiredFieldTMs();

    List<Integer> getCurrentFieldTMs();

    // Field Items

    // TMs on the field

    void setFieldTMs(List<Integer> fieldTMs);

    List<Integer> getRegularFieldItems();

    void setRegularFieldItems(List<Integer> items);

    // Everything else

    void shuffleFieldItems();

    void randomizeFieldItems(boolean banBadItems);

    // Randomizer methods

    List<IngameTrade> getIngameTrades();

    void setIngameTrades(List<IngameTrade> trades);

    // Trades

    void randomizeIngameTrades(boolean randomizeRequest, boolean randomNickname, boolean randomOT,
                               boolean randomStats, boolean randomItem, CustomNamesSet customNames);

    boolean hasDVs();

    int maxTradeNicknameLength();

    int maxTradeOTNameLength();

    void removeTradeEvolutions(boolean changeMoveEvos);

    void condenseLevelEvolutions(int maxLevel, int maxIntermediateLevel);

    // Evos

    void randomizeEvolutions(boolean similarStrength, boolean sameType, boolean limitToThreeStages,
                             boolean forceChange);

    // stats stuff
    void minimumCatchRate(int rateNonLegendary, int rateLegendary);

    void standardizeEXPCurves();

    List<Integer> getGameBreakingMoves();

    // includes game or gen-specific moves like Secret Power
    // but NOT healing moves (Softboiled, Milk Drink)
    List<Integer> getFieldMoves();

    // (Mostly) unchanging lists of moves

    // any HMs required to obtain 4 badges
    // (excluding Gameshark codes or early drink in RBY)
    List<Integer> getEarlyRequiredHMMoves();

    boolean isYellow();

    String getROMName();

    // Misc

    String getROMCode();

    String getSupportLevel();

    String getDefaultExtension();

    int internalStringLength(String string);

    void applySignature();

    boolean isROMHack();

    int generationOfPokemon();

//    public BufferedImage getMascotImage();

    void writeCheckValueToROM(int value);

    int miscTweaksAvailable();

    void applyMiscTweak(MiscTweak tweak);

    // code tweaks

    enum TrainerNameMode {
        SAME_LENGTH, MAX_LENGTH, MAX_LENGTH_WITH_CLASS
    }

    abstract class Factory {
        public RomHandler create(Random random) throws PackageManager.NameNotFoundException {
            return create(random, null);
        }

        public abstract RomHandler create(Random random, PrintStream log) throws PackageManager.NameNotFoundException;

        public abstract boolean isLoadable(String filename);
    }

}
