
'use client';

import React, {useState, useEffect, useCallback, useRef} from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { Progress } from '@/components/ui/progress';
import { Separator } from '@/components/ui/separator';
import { Toaster } from '@/components/ui/toaster';
import { useToast } from '@/hooks/use-toast';
import { Skeleton } from '@/components/ui/skeleton';

// ASCII art for Hangman stages (6 stages + initial gallows)
const HANGMAN_STAGES = [
  `
    +---+
    |   |
        |
        |
        |
        |
  =========`,
  `
    +---+
    |   |
    O   |
        |
        |
        |
  =========`,
  `
    +---+
    |   |
    O   |
    |   |
        |
        |
  =========`,
  `
    +---+
    |   |
    O   |
   /|   |
        |
        |
  =========`,
  `
    +---+
    |   |
    O   |
   /|\\  |
        |
        |
  =========`,
  `
    +---+
    |   |
    O   |
   /|\\  |
   /    |
        |
  =========`,
  `
    +---+
    |   |
    O   |
   /|\\  |
   / \\  |
        |
  =========`,
];

const ALPHABET = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';

// Placeholder for Firebase functions - will be replaced with actual implementation
const fetchWordsFromFirestore = async () => {
  // Simulate fetching words from Firestore
  await new Promise(resolve => setTimeout(resolve, 500)); // Simulate network latency
  return [
    { word: 'javascript', category: 'Programming' },
    { word: 'react', category: 'Programming' },
    { word: 'firebase', category: 'Cloud Services' },
    { word: 'terminal', category: 'Computing' },
    { word: 'hangman', category: 'Games' },
    { word: 'keyboard', category: 'Input Devices' },
    { word: 'developer', category: 'Profession' },
    { word: 'computer', category: 'Devices' },
    { word: 'algorithm', category: 'Concepts' },
    { word: 'database', category: 'Storage' },
    { word: 'frontend', category: 'Web Development' },
    { word: 'backend', category: 'Web Development' },
    { word: 'network', category: 'Connectivity' },
    { word: 'security', category: 'Protection' },
    { word: 'analytics', category: 'Data' },
    { word: 'interface', category: 'UI/UX' },
    { word: 'framework', category: 'Programming' },
    { word: 'component', category: 'UI Elements' },
    { word: 'function', category: 'Programming' },
    { word: 'variable', category: 'Programming' },
  ].sort(() => Math.random() - 0.5); // Shuffle for random selection
};

const saveGameResultToFirestore = async (result: any) => {
  // Simulate saving game result
  console.log('Saving game result:', result);
  await new Promise(resolve => setTimeout(resolve, 300));
};

const fetchLeaderboardFromFirestore = async () => {
  // Simulate fetching leaderboard
  await new Promise(resolve => setTimeout(resolve, 700));
  return [
    { rank: 1, name: 'user1', score: 5, attempts: 2 },
    { rank: 2, name: 'user2', score: 10, attempts: 4 },
    { rank: 3, name: 'user3', score: 7, attempts: 3 },
  ];
};

const MAX_WRONG_GUESSES = 6;

export default function HomePage() {
  const [words, setWords] = useState<{ word: string; category: string }[]>([]);
  const wordsRef = useRef<{ word: string; category: string }[]>([]);
  const [currentWord, setCurrentWord] = useState('');
  const [category, setCategory] = useState('');
  const [guessedLetters, setGuessedLetters] = useState<Set<string>>(new Set());
  const [wrongGuesses, setWrongGuesses] = useState(0);
  const [gameOver, setGameOver] = useState(false);
  const [isWinner, setIsWinner] = useState(false);
  const [inputValue, setInputValue] = useState('');

  const [leaderboard, setLeaderboard] = useState<any[]>([]);
  const [loadingLeaderboard, setLoadingLeaderboard] = useState(true);
  const [loadingWords, setLoadingWords] = useState(true);

  const { toast } = useToast();
  const inputRef = useRef<HTMLInputElement>(null);
  const gameAreaRef = useRef<HTMLDivElement>(null);
  const cursorRef = useRef<HTMLSpanElement>(null);

  // Start a new game
  const startNewGame = useCallback(() => {
    const wordsList = wordsRef.current;
    if (wordsList.length === 0) return;

    const selectedWordData = wordsList[Math.floor(Math.random() * wordsList.length)];
    const selectedWord = selectedWordData.word.toUpperCase();
    setCurrentWord(selectedWord);
    setCategory(selectedWordData.category);

    // Feature: Give at least 3 hint elements in the middle of the word in advance
    const wordLength = selectedWord.length;
    const midIndex = Math.floor(wordLength / 2);

    // Map unique letters to their closest distance to the center of the word
    const letterMap = new Map<string, number>();
    for (let i = 0; i < wordLength; i++) {
      const char = selectedWord[i];
      const distance = Math.abs(i - midIndex);
      if (!letterMap.has(char) || distance < letterMap.get(char)!) {
        letterMap.set(char, distance);
      }
    }

    // Sort unique letters by their proximity to the center of the word
    const sortedLetters = Array.from(letterMap.entries())
      .sort((a, b) => a[1] - b[1])
      .map(entry => entry[0]);

    // Pre-reveal at least 3 letters closest to the middle of the word
    const numToReveal = Math.max(3, Math.min(sortedLetters.length, 3));
    const preGuessed = new Set<string>();
    for (let i = 0; i < Math.min(numToReveal, sortedLetters.length); i++) {
      preGuessed.add(sortedLetters[i]);
    }

    setGuessedLetters(preGuessed);
    setWrongGuesses(0);
    setGameOver(false);
    setIsWinner(false);
    setInputValue('');
    inputRef.current?.focus();
  }, []);

  // Fetch words and leaderboard on mount
  useEffect(() => {
    const initializeGame = async () => {
      try {
        setLoadingWords(true);
        const fetchedWords = await fetchWordsFromFirestore();
        wordsRef.current = fetchedWords;
        setWords(fetchedWords);
        setLoadingWords(false);
        startNewGame();

        setLoadingLeaderboard(true);
        const fetchedLeaderboard = await fetchLeaderboardFromFirestore();
        setLeaderboard(fetchedLeaderboard);
        setLoadingLeaderboard(false);
      } catch (error) {
        console.error("Failed to initialize game:", error);
        toast({
          title: "Error",
          description: "Failed to load game data. Please try again later.",
          variant: "destructive",
        });
        setLoadingWords(false);
        setLoadingLeaderboard(false);
      }
    };
    initializeGame();
  }, [toast, startNewGame]);

  // Simulate blinking cursor
  useEffect(() => {
    const intervalId = setInterval(() => {
      if (cursorRef.current) {
        cursorRef.current.style.opacity =
          cursorRef.current.style.opacity === '0' ? '1' : '0';
      }
    }, 500);
    return () => clearInterval(intervalId);
  }, []);

  // Handle letter guesses
  const handleGuess = useCallback((letter: string) => {
    if (gameOver || !currentWord) return;

    const normalizedLetter = letter.toUpperCase();

    if (guessedLetters.has(normalizedLetter)) {
      toast({ title: "Already guessed", description: `You already guessed "${normalizedLetter}"`, variant: "default"});
      return;
    }

    setGuessedLetters(prev => new Set(prev).add(normalizedLetter));

    if (currentWord.includes(normalizedLetter)) {
      toast({ title: "Correct!", description: `"${normalizedLetter}" is in the word.`, variant: "default"});
      // Check for win condition
      const wordLetters = currentWord.split('');
      const allGuessed = wordLetters.every(l => guessedLetters.has(l) || l === normalizedLetter);
      if (allGuessed) {
        setIsWinner(true);
        setGameOver(true);
        saveGameResultToFirestore({ word: currentWord, won: true, wrongGuesses, timestamp: new Date() });
        toast({ title: "You Win!", description: "Congratulations, you guessed the word!", variant: "default"});
      }
    } else {
      const newWrongGuesses = wrongGuesses + 1;
      setWrongGuesses(newWrongGuesses);

      if (newWrongGuesses >= MAX_WRONG_GUESSES) {
        setIsWinner(false);
        setGameOver(true);
        saveGameResultToFirestore({ word: currentWord, won: false, wrongGuesses: newWrongGuesses, timestamp: new Date() });
        toast({ title: "Game Over", description: `You ran out of attempts! The word was "${currentWord}".`, variant: "destructive"});
      } else {
        toast({ title: "Wrong guess", description: `"${normalizedLetter}" is not in the word.`, variant: "destructive"});
        // Shake animation for the letter buttons - handled by CSS targeting
      }
    }
    setInputValue(''); // Clear input after guess
  }, [currentWord, guessedLetters, wrongGuesses, gameOver, isWinner, toast, saveGameResultToFirestore]);

  // Handle keyboard input
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (value.length > 1) {
      const guess = value[value.length - 1];
      handleGuess(guess);
    } else if (value.length === 1 && /^[a-zA-Z]$/.test(value)) {
      handleGuess(value);
    } else if (value === '') {
      setInputValue('');
    }
  };

  // Handle alphabet button clicks
  const handleAlphabetClick = (letter: string) => {
    handleGuess(letter);
  };

  const displayedWord = currentWord
    .split('')
    .map(letter => (guessedLetters.has(letter) ? letter : '_'))
    .join(' ');

  const isLetterUsed = (letter: string) => guessedLetters.has(letter);

  const getLetterVariant = (letter: string) => {
    if (!currentWord || !guessedLetters.has(letter)) return 'outline';
    return currentWord.includes(letter) ? 'default' : 'destructive';
  };

  const getLetterColorClass = (letter: string) => {
    if (!currentWord || !guessedLetters.has(letter)) return '';
    return currentWord.includes(letter) ? 'text-lime-400' : 'text-red-500';
  }

  // Game Over Message Typing Effect
  const [gameOverMessage, setGameOverMessage] = useState('');
  const gameOverText = isWinner ? 'YOU WIN!' : `YOU DIED! The word was: ${currentWord}`;

  useEffect(() => {
    if (gameOver) {
      setGameOverMessage('');
      let i = 0;
      const intervalId = setInterval(() => {
        setGameOverMessage(prev => prev + gameOverText[i]);
        i++;
        if (i >= gameOverText.length) {
          clearInterval(intervalId);
        }
      }, 100); // Typing speed
      return () => clearInterval(intervalId);
    }
  }, [gameOver, gameOverText]);

  // Render based on loading state
  if (loadingWords || loadingLeaderboard) {
    return (
      <div className="flex items-center justify-center min-h-screen p-4">
        <Card className="w-full max-w-xl">
          <CardHeader>
            <CardTitle className="flex items-center gap-2 font-mono text-lg">
              <span className="text-green-400">hangman@unix:~$</span>
              <span className="text-yellow-400 animate-pulse">./loading...</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col gap-4 items-center justify-center">
            <Skeleton className="h-64 w-full rounded-lg" />
            <Skeleton className="h-12 w-full rounded-lg" />
            <div className="grid grid-cols-3 gap-2 w-full">
              {[...Array(26)].map((_, i) => (
                <Skeleton key={i} className="h-10 w-full rounded-lg" />
              ))}
            </div>
            <Skeleton className="h-10 w-full rounded-lg" />
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="relative flex flex-col items-center justify-center min-h-screen p-4 overflow-hidden terminal-background">
      <div className="absolute inset-0 -z-10 opacity-30 crt-scanlines"></div>
      <Card className="w-full max-w-3xl shadow-2xl backdrop-blur-sm border-green-500/20 bg-black/60">
        <CardHeader className="border-b border-green-500/30 px-6 py-4">
          <CardTitle className="flex items-center justify-between font-mono text-lg text-green-400">
            <span>hangman@unix:~$ ./play</span>
            <span className="text-xs text-gray-400">v1.0</span>
          </CardTitle>
        </CardHeader>
        <CardContent className="p-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            {/* Hangman Display & Game Info */}
            <div className="lg:col-span-1 flex flex-col gap-4">
              <pre className="text-green-500 text-lg font-mono whitespace-pre-wrap border border-green-500/30 p-4 rounded-lg aspect-square flex items-center justify-center overflow-hidden">
                {HANGMAN_STAGES[wrongGuesses]}
              </pre>
              <div className="rounded-lg border border-green-500/30 p-4">
                <p className="font-mono text-sm">Category: <span className="text-lime-400">{category}</span></p>
                <p className="font-mono text-sm mt-2">
                  Attempts Remaining:
                  <span className={`font-bold ${(MAX_WRONG_GUESSES - wrongGuesses) <= 2 ? 'text-red-500' : 'text-green-400'}`}>
                    {' '}{MAX_WRONG_GUESSES - wrongGuesses}/{MAX_WRONG_GUESSES}
                  </span>
                </p>
              </div>
            </div>

            {/* Game Controls & Word Display */}
            <div className="lg:col-span-2 flex flex-col gap-6">
              <div className="text-center">
                <h1 className="text-4xl font-bold font-mono text-green-400 tracking-widest mb-4">
                  {displayedWord}
                </h1>
              </div>

              {gameOver && (
                <Alert variant={isWinner ? "default" : "destructive"} className="border-2 border-solid font-mono">
                  <AlertTitle className={`text-2xl font-bold ${isWinner ? 'text-lime-400' : 'text-red-500'}`}>
                    {isWinner ? 'VICTORY!' : 'DEFEATED!'}
                  </AlertTitle>
                  <AlertDescription className="text-lg flex items-center gap-2">
                    <span className="text-green-400 typing-effect">{gameOverMessage}</span>
                    {!isWinner && <span className="animate-pulse">_</span>}
                  </AlertDescription>
                </Alert>
              )}

              {!gameOver && (
                <div className="flex flex-col gap-4">
                  <div className="flex items-center gap-2 font-mono text-sm">
                    <span className="text-green-400">Input Guess:</span>
                    <Input
                      ref={inputRef}
                      type="text"
                      value={inputValue}
                      onChange={handleInputChange}
                      maxLength={1}
                      placeholder="A-Z"
                      className="w-16 h-8 font-mono text-base text-green-500 border-green-500/50 focus:ring-lime-400 focus:border-lime-400 bg-black/50 placeholder:text-gray-600"
                      autoComplete="off"
                      spellCheck="false"
                      autoCapitalize="characters"
                      onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                          e.preventDefault();
                          if (inputValue) handleGuess(inputValue);
                        }
                      }}
                    />
                    <span ref={cursorRef} className="text-green-500 animate-pulse">_</span>
                  </div>
                  <div className="grid grid-cols-4 sm:grid-cols-5 lg:grid-cols-7 gap-2">
                    {ALPHABET.split('').map((letter) => (
                      <Button
                        key={letter}
                        variant={getLetterVariant(letter)}
                        size="sm"
                        disabled={isLetterUsed(letter) || gameOver}
                        onClick={() => handleAlphabetClick(letter)}
                        className={`font-mono text-lg uppercase transition-all duration-150 ease-in-out
                          ${isLetterUsed(letter) ? (currentWord.includes(letter) ? 'bg-lime-500 hover:bg-lime-600 text-black' : 'bg-red-600 hover:bg-red-700 text-white') : ''}
                          ${!isLetterUsed(letter) && !gameOver ? 'hover:scale-110 hover:shadow-lg' : ''}
                          ${isLetterUsed(letter) && currentWord.includes(letter) ? 'cursor-default' : ''}
                          ${isLetterUsed(letter) && !currentWord.includes(letter) ? 'cursor-not-allowed' : ''}
                          ${!isLetterUsed(letter) && !gameOver && 'hover:bg-green-600'}
                          ${!isLetterUsed(letter) && !gameOver ? 'shadow-md' : ''}
                          ${isLetterUsed(letter) && currentWord.includes(letter) ? 'border-lime-500/50' : ''}
                          ${isLetterUsed(letter) && !currentWord.includes(letter) ? 'border-red-500/50' : ''}
                          ${!isLetterUsed(letter) && !gameOver ? 'border-green-500/50' : ''}
                        `}
                      >
                        <span className={getLetterColorClass(letter)}>
                          {letter}
                        </span>
                      </Button>
                    ))}
                  </div>
                </div>
              )}

              {/* New Game Button */}
              <div className="mt-4 pt-4 border-t border-green-500/30 text-center">
                <Button
                  variant="outline"
                  size="lg"
                  onClick={startNewGame}
                  className="font-mono text-green-400 hover:text-green-200 hover:border-green-400/50 hover:bg-black/40 shadow-md"
                  data-testid="new-game-button"
                >
                  $ ./new-game
                </Button>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Leaderboard Section (Optional, can be toggled or placed elsewhere) */}
      <div className="mt-8 w-full max-w-3xl">
        <Card className="shadow-2xl backdrop-blur-sm border-blue-500/20 bg-black/50">
          <CardHeader className="border-b border-blue-500/30 px-6 py-4">
            <CardTitle className="flex items-center justify-between font-mono text-lg text-blue-400">
              <span>hangman@unix:~$ cat leaderboard</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-6">
            {loadingLeaderboard ? (
              <div className="flex flex-col gap-2">
                <Skeleton className="h-8 w-full rounded-lg" />
                <Skeleton className="h-8 w-full rounded-lg" />
                <Skeleton className="h-8 w-full rounded-lg" />
              </div>
            ) : leaderboard.length > 0 ? (
              <ol className="list-decimal list-inside font-mono text-sm space-y-2 text-gray-300">
                {leaderboard.map((entry, index) => (
                  <li key={index} className="flex justify-between items-center">
                    <span>{entry.rank}. {entry.name}</span>
                    <span className="text-xs text-gray-400">
                      ({entry.attempts} wrong guesses)
                    </span>
                  </li>
                ))}
              </ol>
            ) : (
              <p className="text-center text-gray-500 italic">Leaderboard is empty.</p>
            )}
          </CardContent>
        </Card>
      </div>

      <Toaster />
    </div>
  );
}
