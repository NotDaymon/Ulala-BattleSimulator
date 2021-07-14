package com.company;
import java.lang.annotation.Target;
import java.util.Scanner;

public class Main {
    public class Character{
        public int ATK = 20000;
        public float SPD = 2.35f;
        public Character(int atk, float spd){
            this.ATK = atk;
            this.SPD = spd;
        }
        public Character(){
        }
    }
    public class Spell{
        public String Name;
        public int Cost;
        public int FlatDamage;
        public double PercentDamage;
        public float CastingTime;
        public boolean ColdStack = false;
        public int Targets = 1;
        public double BonusDamage;
        public Spell(String name, int cost, int flatDamage, double percentDamage, float castingTime, boolean coldStack, int targets){
            this.Name = name;
            this.Cost = cost;
            this.FlatDamage = flatDamage;
            this.PercentDamage = percentDamage;
            this.CastingTime = castingTime;
            this.ColdStack = coldStack;
            this.Targets = targets;
        }
        public Spell(String name, int cost, int flatDamage, double percentDamage, float castingTime){
            this.Name = name;
            this.Cost = cost;
            this.FlatDamage = flatDamage;
            this.PercentDamage = percentDamage;
            this.CastingTime = castingTime;
        }
        public Spell Effect(Spell nextSpell, Character character){
            return nextSpell;
        }
    }
    final float COLDSTACK_PERCENTAGE = .3f;
    final int SIMULATIONS = 10000;
    public static void main(String[] args) {
        Main main = new Main();
        main.Battle();
    }

    public void Battle(){
        Character Mage = new Character();

        Spell Serenity = new Spell("Serenity", 1, 728, 140.0, 0.0f){
            public Spell Effect(Spell nextSpell, Character character){
                nextSpell.BonusDamage+=(this.FlatDamage + this.PercentDamage*character.ATK);
                return nextSpell;
            }
        };
        Spell Meteorite = new Spell("Meteorite", 4, 945, 181.0, 2.28f, false, 3);
        Spell MagicIntellect = new Spell("Magic Intellect", 3, 1080, 207, 0.96f){
            public Spell Effect(Spell nextSpell, Character character){
                nextSpell.CastingTime = 0.0f;
                return nextSpell;
            }
        };
        Spell FrostNova = new Spell("Frost Nova", 3, 384, 74.0, 2.28f, true, 4);
        Spell Blizzard = new Spell("Blizzard", 4, 239, 46.0, 3.30f, true, 4);

        Spell[] magicIntDeck = {FrostNova,Serenity,Blizzard,MagicIntellect};
        Spell[] meteoriteDeck = {FrostNova,Serenity,Blizzard,Meteorite};
        double magicIntTotalDamage = 0;
        double meteroiteTotalDamage = 0;
        for(int i=0;i<SIMULATIONS;i++){
            magicIntTotalDamage+=BattleAOEBots(magicIntDeck, Mage);
            meteroiteTotalDamage+=BattleAOEBots(meteoriteDeck, Mage);
        }
        System.out.printf("We ran " + SIMULATIONS + " simulations, and ");
        if(magicIntTotalDamage>meteroiteTotalDamage){
            System.out.printf("the Magic Intellect deck won by %,.2f damage, or %.2f%%",(magicIntTotalDamage-meteroiteTotalDamage),((magicIntTotalDamage/meteroiteTotalDamage)*100));
        }else{
            System.out.printf("the Meteorite deck won by %,.2f damage, or %.2f%%",(meteroiteTotalDamage-magicIntTotalDamage),((meteroiteTotalDamage/magicIntTotalDamage)*100));
        }
    }

    public double BattleAOEBots(Spell[] deck, Character character){
        float timer = 50.0f;
        int mana = 0;
        double damageDone = 0.0;
        int deckIndex = 0;
        float coldStack = 0.0f;
        while(timer>0.0f){
            if(deck[deckIndex].Cost<=mana){
                Spell currentCard = deck[deckIndex];
                mana-=currentCard.Cost;
                timer-=currentCard.CastingTime;
                if(timer>0.0f){
                    double damage = 0;
                    damage+= currentCard.Targets*((currentCard.FlatDamage) + (character.ATK* currentCard.PercentDamage));
                    if(currentCard.ColdStack){
                        damage += damage*coldStack;
                        coldStack+=COLDSTACK_PERCENTAGE;
                    }
                    damage+= currentCard.BonusDamage;
                    currentCard.BonusDamage=0;
                    damageDone+=damage;
                }
                deckIndex++;
                if(deckIndex==4) deckIndex=0;
            }else{
                timer-=character.SPD;
                mana++;
                if(timer>0.0f) damageDone+=character.ATK;
            }
        }
        return damageDone;
    }
}
