import type {Metadata} from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Unix Hangman',
  description: 'A classic Hangman game with a Unix terminal feel.',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=VT323&display=swap" rel="stylesheet" />
      </head>
      <body className="font-body antialiased text-green-500 bg-black/90">{children}</body>
    </html>
  );
}
