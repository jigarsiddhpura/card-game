import java.util.*;
import java.io.*;

class Player {
    String name;
    List<Card> cards = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void addCard(String cardstr) {
        this.cards.add(new Card(cardstr.substring(0, 1), cardstr.substring(1)));
    }
}

class Card implements Comparable<Card> {
    String suit;
    String val;

    public static final Map<String, Integer> suitRanks = Map.of("c", 3, "d", 2, "h", 1, "s", 0);
    public static final Map<String, Integer> cardRanks = new HashMap<>();

    static {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "j", "q", "k", "a"};
        for (int i = 0; i < ranks.length; i++) {
            cardRanks.put(ranks[i], i);
        }
    }

    public Card(String suit, String val) {
        this.suit = suit;
        this.val = val;
    }
    
    @Override
    public int compareTo(Card c2) {
        return Integer.compare(cardRanks.get(this.val), cardRanks.get(c2.val));
    }

    @Override
    public String toString() {
        return suit + val;
    }
}

class Game {
    Map<String, Player> players = new HashMap<>();
    List<Card> playedCards = new ArrayList<>();
    String trump;
    List<String> playerNames;
    String roundSuit;

    Game(List<String> playerNames, String trump) {
        for (String name : playerNames) {
            players.put(name, new Player(name));
        }
        this.trump = trump;
        this.playerNames = playerNames;
    }

    void addCardToPlayer(String playerName, String cardStr) {
        Card card = new Card(cardStr.substring(0, 1), cardStr.substring(1));
        players.get(playerName).addCard(cardStr);
        playedCards.add(card);
        if (playedCards.size() == 1) roundSuit = card.suit;
    }

    public Card getOptimalCard(Player self) {
        int teammateIdx = (playerNames.indexOf(self.name) + 2) % 4;
        String teammate = playerNames.get(teammateIdx);

        Card highestCard = getHighestCard(playedCards);

        // Scenario 1: If the teammate has the highest card (win is fixed), play the smallest card
        if (players.get(teammate).cards.contains(highestCard)) {
            return getSmallestCardOverall(self.cards);
        }

        // Scenario 2: if i can win (smallest to win)
        Card winningCard = getSmallestCardToWin(self.cards);
        if (winningCard != null) return winningCard;

        // Scenario 3: fallback (lose is fixed)
        return getSmallestCardOverall(self.cards);
    }

    public Card getHighestCard(List<Card> cards) {
        return cards.stream()
        .max((c1,c2) -> {
            if (c1.suit.equals(trump) && !c2.suit.equals(trump)) return 1;
            if (!c1.suit.equals(trump) && c2.suit.equals(trump)) return -1;
            if (c1.suit.equals(trump) && c2.suit.equals(trump)) return c1.compareTo(c2);
            if (c1.suit.equals(roundSuit) && !c2.suit.equals(roundSuit)) return 1;
            if (!c1.suit.equals(roundSuit) && c2.suit.equals(roundSuit)) return -1;
            if (c1.suit.equals(roundSuit) && c2.suit.equals(roundSuit)) return c1.compareTo(c2);
            if (!c1.suit.equals(roundSuit) && !c2.suit.equals(roundSuit)) return c1.compareTo(c2);
            return 0;
        })
        .orElse(null);
    }

    public Card getHighestCard(List<Card> cards, String suit) {
        return cards.stream()
            .filter(c -> c.suit.equals(suit))
            .max(Card::compareTo)
            .orElse(null);
    }

    public Card getSmallestCardBiggerThan(List<Card> cards, Card targetCard, String targetSuit) {
        return cards.stream()
            .filter(c -> c.suit.equals(targetSuit) && c.compareTo(targetCard) > 0)
            .min(Card::compareTo)
            .orElse(null);
    }

    public Card getSmallestCardToWin(List<Card> cards) {
        Card highestPlayed = getHighestCard(cards);

        if (!highestPlayed.suit.equals(trump)) {
            Card smallestWinningCard = getSmallestCardBiggerThan(cards, highestPlayed, roundSuit);
            if (smallestWinningCard != null) return smallestWinningCard;

            return getSmallestCardOfSuit(cards, trump);
        } else {    
            return getSmallestCardBiggerThan(cards, highestPlayed, trump);
        }
    }

    public Card getSmallestCardOverall(List<Card> cards) {

        List<Card> roundSuitCards = cards.stream()
        .filter(c -> c.suit.equals(roundSuit))
        .sorted()
        .toList();

        if (!roundSuitCards.isEmpty()) return roundSuitCards.get(0);

        List<Card> nonTrumpCards = cards.stream()
        .filter(c -> !c.suit.equals(trump))
        .sorted((c1,c2) -> {
            if (c1.compareTo(c2) != 0) return c1.compareTo(c2);
            return Card.suitRanks.get(c1.suit).compareTo(Card.suitRanks.get(c2.suit));
        })
        .toList();

        if (!nonTrumpCards.isEmpty()) return nonTrumpCards.get(0);

        return cards.stream().min(Card::compareTo).orElse(null);
    }

    public Card getSmallestCardOfSuit(List<Card> cards, String suit) {
        return cards.stream()
        .filter(c -> c.suit.equals(suit))
        .min(Card::compareTo)
        .orElse(null);
    }
}

public class Gupshup2 {
    public static void main(String args[]) {
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(System.out)
            ) {

            String input = br.readLine();
            Map<String, String> inputMap = new HashMap<>();

            for (String component : input.split(";")) {
                String[] keyValue = component.split(":");
                if (keyValue.length == 2) {
                    inputMap.put(keyValue[0], keyValue[1]);
                }
            }

            List<String> playerNames = Arrays.asList(inputMap.get("players").split(","));
            String trump = inputMap.get("trump");
            Game game = new Game(playerNames, trump);

            for (String playerName : playerNames) {
                if (inputMap.containsKey(playerName)) {
                    game.addCardToPlayer(playerName, inputMap.get(playerName));
                }
                if (playerName.equals(inputMap.get("self"))) break;
            }

            Player self = game.players.get(inputMap.get("self"));
            String cardsString = inputMap.get("cards");
            
            if (cardsString != null && !cardsString.isEmpty()) {
                for (String cardStr : cardsString.split(",")) {
                    self.addCard(cardStr);
                }
            }

            Card ans = game.getOptimalCard(self);
            out.println(ans.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}