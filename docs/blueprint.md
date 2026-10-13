# **App Name**: Unix Hangman

## Core Features:

- Word Bank: Stores words and categories in Firestore for game initialization.
- Game State Management: Manages the current word, guessed letters, remaining attempts, and hangman state.
- Letter Guessing: Allows players to guess letters via on-screen buttons or keyboard input, updating the UI.
- Hangman Drawing: Renders ASCII art stages of the hangman progressively based on wrong guesses.
- Win/Lose Detection: Determines and displays win or lose conditions ('YOU DIED' or success message).
- Game History: Saves game results (word, win status, wrong guesses) to Firestore.
- Leaderboard: Displays a real-time leaderboard of top scores from Firestore.

## Style Guidelines:

- Dark background (#0d0d0d) evoking a classic terminal.
- Primary text and elements: Vibrant green (#00FF00) for high contrast and a retro feel.
- Accent for correct guesses: Lime green (#7FFF00).
- Accent for wrong guesses: Blood red (#FF0000).
- Primary font: 'VT323' (monospace) for an authentic retro terminal appearance.
- Code font: 'VT323' (monospace) for all terminal-like text displays.
- ASCII art used exclusively for the hangman figure, presented in a `<pre>` block.
- Terminal header bar displaying 'hangman@unix:~$ ./play'.
- Main content area structured for readability: hangman display, word display, guess buttons, and status info.
- CRT scanline and flicker effect applied to the background for a vintage display feel.
- Blinking cursor simulation for input areas and prompts.
- Typing effect for game-over messages, displaying letters incrementally.
- Subtle 'typing' animation for correctly guessed letters appearing in the word display.
- Letter buttons subtly shake upon incorrect guesses.