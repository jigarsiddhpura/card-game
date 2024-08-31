import java.util.*;
import java.io.*;


class Card implements Comparable<Card> {
    String suit;
    String val;

    public static final Map<String, Integer> suitRanks = new HashMap<>();
    public static final Map<String, Integer> cardRanks = new HashMap<>();

    static {
        suitRanks.put("c",3);
        suitRanks.put("d",2);
        suitRanks.put("h",1);
        suitRanks.put("s",0);

        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k", "a"};
        for (int i=0; i<ranks.length; i++) {
            cardRanks.put(ranks[i], i);
        }
    }

    public Card(String suit, String val) {
        this.suit = suit;
        this.val = val;
    }
    
    @Override
    public int compareTo(Card c2) {
        int suitComparison = Integer.compare(suitRanks.get(this.suit), suitRanks.get(c2.suit));

        if (suitComparison != 0) return suitComparison;
        else return Integer.compare(cardRanks.get(this.val), cardRanks.get(c2.val));
    }

    @Override
    public String toString() {
        return suit+val;
    }
}



class Game {
    // check if selfs' win is fixed or not
    String trumpSuit, self, roundSuit;
    String[] players;
    HashMap<String, Card> plays;
    List<Card> cardsToChooseFrom;
    List<Card> cards;

    public Game(String[] players, List<Card> cardsToChooseFrom, String trump, HashMap<String, Card> plays, String self) {
        this.players = players;
        this.trumpSuit = trump;
        this.self = self;
        this.cardsToChooseFrom = cardsToChooseFrom;
        this.plays = plays;
        this.cards = new ArrayList<>(plays.values());
        this.roundSuit = plays.get(players[0]).suit;
    }

    // get biggest card
    public Card getBiggestCard() {
        Collections.sort(cards);
        return cards.get(cards.size() - 1);
    }

    public boolean hasSomeonePlayedTrump() {
        for(Card c : cards) {
            if (c.suit == trumpSuit) return true;
        }
        return false;
    }

    public boolean hasCardOfSuitRound(List<Card> li) {
        for(Card c : li) {
            if (c.suit == roundSuit) return true;
        }
        return false;
    }

    public boolean hasCardOtherThanTrump() {
        for(Card c : cardsToChooseFrom) {
            if (c.suit != trumpSuit) return true;
        }
        return false;
    }

    public Card getOptimumCard() {
        
        boolean isWinFixed = false;
        boolean isLoseFixed = false;
        String player2 = players[1];

        if (isWinFixed || isLoseFixed) {

        }
    }
}

public class Gupshup {
    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        // StringTokenizer st;

        try {
            String input = br.readLine();
            String[] components = input.split(";");
            HashMap<String, Card> plays = new HashMap<>();
            String[] players = new String[4];
            List<Card> cardsToChooseFrom = new ArrayList<>();
            String trumpSuit = "", self = "";

            for(String comp : components) {
                String[] pair = comp.trim().split(":", 2);
                if (pair.length == 2) {
                    String key = pair[0].trim();
                    String val = pair[1].trim();

                    switch (key) {
                        case "Players":
                            players = val.split(",");
                            break;
                        case "self":
                            self = val;
                            break;
                        case "trump":
                            trumpSuit = val;
                            break;
                        case "cards":
                            String[] cards = val.split(",");
                            for (String card : cards) {
                                cardsToChooseFrom.add(new Card(card.substring(0, 1), card.substring(1)));
                            }
                            break;
                        default:
                            String suit = val.substring(0,1);
                            String num = val.substring(1);
                            plays.put(key, new Card(suit, num)); 
                            break;
                    }
                } else {
                    out.println("length is not 2");
                }
            }

            Game game = new Game(players, cardsToChooseFrom, trumpSuit, plays, self);
            Card ans = game.getOptimumCard();
            out.println(ans);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}