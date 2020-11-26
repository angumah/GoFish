import java.util.*;

public class GoFish {

    private final String[] SUITS = { "C", "D", "H", "S" };
    private final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

    private String whoseTurn;
    private final Player playerOne;
    private Player playerTwo;
    private static boolean multiplayer;
    private List<Card> deck;
    private final Scanner in;
    private String[] computerCards;

    public GoFish() {
        this.whoseTurn = "P1";
        this.playerOne = new Player();
        this.playerTwo = new Player();
        this.in = new Scanner(System.in);
        this.computerCards = new String[7];
        setup();
    }

    public void setup(){
        /* Determines if game will be multiplayer */
        String playerOrComputer = " ";
        boolean notValidInput = true;
        while(notValidInput) {
            System.out.print("Play against player or computer? type \"p\" for player or \"c\" for computer: ");
            playerOrComputer = in.nextLine();
            if(playerOrComputer.equals("p") || playerOrComputer.equals("c")){
                notValidInput = false;
            }
        }

        switch (playerOrComputer){
            case "p": multiplayer = true;
            break;

            case "c": multiplayer = false;
            break;
        }
    }



    public void play() {
        shuffleAndDeal();

        // play the game until someone wins
            while (true) {
                if (whoseTurn.equals("P1")) {
                    whoseTurn = takeTurn("P1");

                    if (playerOne.findAndRemoveBooks()) {
                        System.out.println("PLAYER 1: Oh, that's a book!");
                        showBooks("P1");
                    }
                } else if (whoseTurn.equals("P2")) {
                    whoseTurn = takeTurn("P2");

                    if (playerTwo.findAndRemoveBooks()) {
                        System.out.println("PLAYER 2: Oh, that's a book!");
                        showBooks("P2");
                    }
                }

                // the games doesn't end until all 13 books are completed, or there are
                // no more cards left in the deck. the player with the most books at the
                // end of the game wins.

                int playerBooks = playerOne.getBooks().size();
                int computerBooks = playerTwo.getBooks().size();

                String winMessage = "Congratulations, you win! " + playerBooks + " books to " + computerBooks + ".";
                String loseMessage = "Maybe next time. You lose " + computerBooks + " books to " + playerBooks + ".";
                String tieMessage = "Looks like it's a tie, " + playerBooks + " to " + computerBooks + ".";

                if (playerBooks + computerBooks == 13) {
                    if (playerOne.getBooks().size() > playerTwo.getBooks().size()) {
                        System.out.println("\n" + winMessage);
                    } else {
                        System.out.println("\n" + loseMessage);
                    }
                    break;
                } else if (deck.size() == 0) {
                    System.out.println("\nOh no, there are no more cards in the deck!");

                    if (playerBooks > computerBooks) {
                        System.out.println(winMessage);
                    } else if (computerBooks > playerBooks) {
                        System.out.println(loseMessage);
                    } else {
                        System.out.println(tieMessage);
                    }
                    break;
                }
            }
        }

    public void shuffleAndDeal() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);  // shuffles the deck

        while (playerOne.getHand().size() < 7) {
            playerOne.takeCard(deck.remove(0));    // deal 7 cards to the
            playerTwo.takeCard(deck.remove(0));  // player and the computer
        }
    }

    ////////// PRIVATE METHODS /////////////////////////////////////////////////////

    private void initializeDeck() {
        deck = new ArrayList<>(52);

        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(rank, suit));     // adds 52 cards to the deck (13 ranks, 4 suits)
            }
        }
    }

    private String takeTurn(String player) {
        if (player.equals("P1")) {
            showHand("P1");
            showBooks("P1");
        }

        else if(player.equals("P2")){
            showHand("P2");
            showBooks("P2");
        }

        // if requestCard returns null, then the hand was empty and new card was drawn.
        // this restarts the turn, ensuring the updated hand is printed to the console.

        Card card = requestCard(player);
        if (card == null) {
            boolean isPlayerOne = player.equals("P1");
            return isPlayerOne ? "P1" : "P2";
            // restart this turn with updated hand
        }

        // check if your opponent has the card you requested. it will be automatically
        // relinquished if you do. otherwise, draw from the deck. return the character
        // code for whose turn it should be next.

        if (player.equals("P1")) {
            if (playerTwo.hasCard(card)) {
                System.out.println("Player 2: Yup, here you go!");
                playerTwo.relinquishCard(playerOne, card);

                return "P1";
            } else {
                System.out.println("Player 2: Nope, go fish!");
                playerOne.takeCard(deck.remove(0));

                return "P2";
            }
        } else {
            if (playerOne.hasCard(card)) {
                System.out.println("Player 1: Yup, here you go!");
                playerOne.relinquishCard(playerTwo, card);

                return "P2`";
            } else {
                System.out.println("Player 1: Nope, go fish!");
                playerTwo.takeCard(deck.remove(0));

                rememberCard(card);

                return "P1";
            }
        }
    }

    private void rememberCard(Card card){

    }

    private Card requestCard(String player) {
        Card card = null;

        // request a card from your opponent, ensuring that the request is valid.
        // if your hand is empty, we return null to signal the calling method to
        // restart the turn. otherwise, we return the requested card.

        while (card == null) {
            if (player.equals("P1")) {
                if (playerOne.getHand().size() == 0) {
                    playerOne.takeCard(deck.remove(0));

                    return null;
                } else {
                    System.out.println("PLAYER 1: Got any... ");
                    String rank = in.nextLine().trim().toUpperCase();
                    card = Card.getCardByRank(rank);
                }
            } else {
                if (playerTwo.getHand().size() == 0) {
                    playerTwo.takeCard(deck.remove(0));

                    return null;
                } else {
                    if(multiplayer) {
                        System.out.println("Player 2: Got any... ");
                        String rank = in.nextLine().trim().toUpperCase();
                        card = Card.getCardByRank(rank);
                    }
                    else {
                        card = playerTwo.getCardByNeed();
                        System.out.println("Player 2: Got any... " + card.getRank());
                    }
                }
            }
        }

        return card;
    }

    private void showHand(String player) {
        if (player.equals("P1")) {
            System.out.println("\nPLAYER 1 hand: " + playerOne.getHand());   // only show player's hand
        }
        else if(player.equals("P2")){
            System.out.println("\nPLAYER 2 hand: " + playerTwo.getHand());   // only show player's hand
        }
    }

    private void showBooks(String player) {
        if (player.equals("P1")) {
            System.out.println("PLAYER books: " + playerOne.getBooks());   // shows the player's books
        } else if(player.equals("P2")){
            System.out.println("\nPlayer 2 books: " + playerTwo.getBooks());  // shows the computer's books
        }
    }














    ////////// MAIN METHOD /////////////////////////////////////////////////////////

    public static void main(String[] args) {
        System.out.println("#########################################################");
        System.out.println("#                                                       #");
        System.out.println("#   ####### #######   ####### ####### ####### #     #   #");
        System.out.println("#   #       #     #   #          #    #       #     #   #");
        System.out.println("#   #  #### #     #   #####      #    ####### #######   #");
        System.out.println("#   #     # #     #   #          #          # #     #   #");
        System.out.println("#   ####### #######   #       ####### ####### #     #   #");
        System.out.println("#                                                       #");
        System.out.println("#   A human v. CPU rendition of the classic card game   #");
        System.out.println("#   Go Fish. Play the game, read and modify the code,   #");
        System.out.println("#   and make it your own!                               #");
        System.out.println("#                                                       #");
        System.out.println("#########################################################");

        new GoFish().play();
    }



}