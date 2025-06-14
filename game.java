import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TimeBasedQuizGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel, welcomePanel, quizPanel, resultPanel;
    private JLabel welcomeLabel, questionLabel, scoreLabel, timerLabel, resultLabel;
    private JButton startButton, nextButton, prevButton, restartButton, exitButton;
    private ButtonGroup optionGroup;
    private JRadioButton[] options;
    private Timer timer;
    private int currentQuestionIndex = 0;
    private int correctAnswersCount = 0;
    private int wrongAnswersCount = 0;
    private int totalScore = 0;
    private int totalTime = 0;
    private int timeRemaining = 20;
    private boolean[] answeredQuestions;

    private final String[] questions = {
            "What is the capital of France?",
            "Which planet is known as the Red Planet?",
            "What is the largest mammal in the world?",
            "In which year did World War II end?",
            "Who wrote 'Romeo and Juliet'?"
    };

    private final String[][] optionsArray = {
            {"Paris", "Berlin", "London", "Rome"},
            {"Mars", "Venus", "Jupiter", "Saturn"},
            {"Elephant", "Blue Whale", "Giraffe", "Hippopotamus"},
            {"1945", "1939", "1941", "1950"},
            {"William Shakespeare", "Charles Dickens", "Jane Austen", "Mark Twain"}
    };

    private final int[] correctAnswers = {0, 0, 1, 0, 0}; // Index of correct options (0-based)

    public TimeBasedQuizGUI() {
        setTitle("Time-based Quiz");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
       
        answeredQuestions = new boolean[questions.length];

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Welcome Panel
        welcomePanel = new JPanel(new BorderLayout(10, 10));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        welcomeLabel = new JLabel("<html><center><h1>Welcome to the Quiz!</h1>"
                + "<p>Test your knowledge with this timed quiz.</p>"
                + "<p>You'll have 20 seconds per question.</p></center></html>", JLabel.CENTER);
        startButton = new JButton("Start Quiz");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(e -> startQuiz());

        // Quiz Panel
        quizPanel = new JPanel(new BorderLayout(10, 10));
        quizPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
       
        questionLabel = new JLabel("", JLabel.CENTER);
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
       
        scoreLabel = new JLabel("", JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 14));
       
        timerLabel = new JLabel("", JLabel.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.BLUE);

        optionGroup = new ButtonGroup();
        options = new JRadioButton[4];
       
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setFont(new Font("Arial", Font.PLAIN, 14));
            optionGroup.add(options[i]);
            optionsPanel.add(options[i]);
        }

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 14));
        nextButton.addActionListener(e -> nextQuestion());

        prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Arial", Font.BOLD, 14));
        prevButton.addActionListener(e -> previousQuestion());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);

        // Result Panel
        resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        resultLabel = new JLabel("", JLabel.CENTER);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));
       
        restartButton = new JButton("Restart Quiz");
        restartButton.addActionListener(e -> restartQuiz());
       
        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
       
        JPanel resultButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        resultButtonPanel.add(restartButton);
        resultButtonPanel.add(exitButton);

        // Add components to panels
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);
        welcomePanel.add(startButton, BorderLayout.SOUTH);
       
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(questionLabel, BorderLayout.NORTH);
        centerPanel.add(optionsPanel, BorderLayout.CENTER);
       
        quizPanel.add(centerPanel, BorderLayout.CENTER);
        quizPanel.add(buttonPanel, BorderLayout.SOUTH);
       
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(scoreLabel);
        infoPanel.add(timerLabel);
        quizPanel.add(infoPanel, BorderLayout.NORTH);
       
        resultPanel.add(resultLabel, BorderLayout.CENTER);
        resultPanel.add(resultButtonPanel, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(quizPanel, "quiz");
        mainPanel.add(resultPanel, "result");
       
        add(mainPanel);
    }

    private void setupLayout() {
        // Set colors and styles
        welcomePanel.setBackground(new Color(240, 248, 255));
        quizPanel.setBackground(new Color(245, 245, 245));
        resultPanel.setBackground(new Color(240, 248, 255));
       
        startButton.setBackground(new Color(50, 150, 250));
        startButton.setForeground(Color.WHITE);
        nextButton.setBackground(new Color(50, 150, 250));
        nextButton.setForeground(Color.WHITE);
        prevButton.setBackground(new Color(150, 150, 150));
        prevButton.setForeground(Color.WHITE);
        restartButton.setBackground(new Color(50, 150, 250));
        restartButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(220, 80, 80));
        exitButton.setForeground(Color.WHITE);
    }

    private void startQuiz() {
        // Reset all counters and states
        currentQuestionIndex = 0;
        correctAnswersCount = 0;
        wrongAnswersCount = 0;
        totalScore = 0;
        totalTime = 0;
        timeRemaining = 20;
        answeredQuestions = new boolean[questions.length];
       
        cardLayout.show(mainPanel, "quiz");
        showQuestion();
       
        // Start timer
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, e -> {
            timeRemaining--;
            totalTime++;
            updateTimerLabel();
            if (timeRemaining <= 0) {
                checkAnswer();
                nextQuestion();
            }
        });
        timer.start();
    }

    private void nextQuestion() {
        checkAnswer(); // Check if current question was answered
       
        if (currentQuestionIndex < questions.length - 1) {
            currentQuestionIndex++;
            showQuestion();
        } else {
            endQuiz();
        }
    }

    private void previousQuestion() {
        checkAnswer(); // Check if current question was answered
       
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion();
        }
    }

    private void showQuestion() {
        questionLabel.setText("Question " + (currentQuestionIndex + 1) + ": " + questions[currentQuestionIndex]);
       
        for (int i = 0; i < 4; i++) {
            options[i].setText(optionsArray[currentQuestionIndex][i]);
        }
       
        // Clear selection unless this question was already answered
        if (!answeredQuestions[currentQuestionIndex]) {
            optionGroup.clearSelection();
        }
       
        timeRemaining = 20; // Reset time for this question
        updateTimerLabel();
        updateScoreLabel();
       
        // Enable/disable navigation buttons
        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setText(currentQuestionIndex == questions.length - 1 ? "Finish" : "Next");
    }

    private void checkAnswer() {
        // Don't check if already answered or no selection made
        if (answeredQuestions[currentQuestionIndex] || optionGroup.getSelection() == null) {
            return;
        }
       
        int selectedOption = -1;
        for (int i = 0; i < 4; i++) {
            if (options[i].isSelected()) {
                selectedOption = i;
                break;
            }
        }

        if (selectedOption == correctAnswers[currentQuestionIndex]) {
            correctAnswersCount++;
            totalScore += 3;
        } else {
            wrongAnswersCount++;
        }
       
        answeredQuestions[currentQuestionIndex] = true;
        updateScoreLabel();
    }

    private void updateScoreLabel() {
        scoreLabel.setText(String.format("Correct: %d | Wrong: %d | Score: %d",
                correctAnswersCount, wrongAnswersCount, totalScore));
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("Time Remaining: %ds | Total Time: %ds",
                timeRemaining, totalTime));
       
        // Change color when time is running low
        if (timeRemaining <= 5) {
            timerLabel.setForeground(Color.RED);
        } else {
            timerLabel.setForeground(Color.BLUE);
        }
    }

    private void endQuiz() {
        timer.stop();
       
        // Check if last question was answered
        if (!answeredQuestions[currentQuestionIndex] && optionGroup.getSelection() != null) {
            checkAnswer();
        }
       
        // Calculate percentage
        double percentage = (double) correctAnswersCount / questions.length * 100;
       
        // Prepare result message
        String message = String.format("<html><center><h1>Quiz Completed!</h1>" +
                "<p>Correct Answers: %d</p>" +
                "<p>Wrong Answers: %d</p>" +
                "<p>Total Score: %d</p>" +
                "<p>Percentage: %.1f%%</p>" +
                "<p>Total Time: %d seconds</p></center></html>",
                correctAnswersCount, wrongAnswersCount, totalScore, percentage, totalTime);
       
        resultLabel.setText(message);
        cardLayout.show(mainPanel, "result");
    }

    private void restartQuiz() {
        cardLayout.show(mainPanel, "welcome");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TimeBasedQuizGUI quiz = new TimeBasedQuizGUI();
            quiz.setVisible(true);
        });
    }
}
	

