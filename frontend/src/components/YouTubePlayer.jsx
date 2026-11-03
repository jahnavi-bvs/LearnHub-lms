import { useEffect, useRef, useState } from 'react';

// YouTube's IFrame Player API is one script shared by the whole page, so it's loaded only once.
let apiPromise = null;

function loadYouTubeApi() {
  if (!apiPromise) {
    apiPromise = new Promise((resolve, reject) => {
      if (window.YT?.Player) {
        resolve(window.YT);
        return;
      }
      window.onYouTubeIframeAPIReady = () => resolve(window.YT);
      const script = document.createElement('script');
      script.src = 'https://www.youtube.com/iframe_api';
      script.onerror = () => {
        apiPromise = null; // allow a retry on the next attempt
        reject(new Error('Could not load YouTube'));
      };
      document.head.appendChild(script);
    });
  }
  return apiPromise;
}

/**
 * Plays a video inside the page. The API (rather than a plain iframe) is used because it reports
 * errors, such as a video whose owner has disabled embedding, so a helpful message can be shown.
 * Give this component key={videoId} so a new video starts with a fresh player and no old error.
 */
export default function YouTubePlayer({ videoId, title }) {
  const containerRef = useRef(null);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    let player = null;
    let cancelled = false;
    const container = containerRef.current;

    loadYouTubeApi()
      .then((YT) => {
        if (cancelled) return;
        const host = document.createElement('div'); // the API replaces this element with an iframe
        container.replaceChildren(host);
        player = new YT.Player(host, {
          videoId,
          playerVars: { rel: 0 },
          events: {
            onReady: (event) => event.target.getIframe().setAttribute('title', title),
            onError: () => setFailed(true),
          },
        });
      })
      .catch(() => { if (!cancelled) setFailed(true); });

    return () => {
      cancelled = true;
      player?.destroy?.();
      container.replaceChildren();
    };
  }, [videoId, title]);

  const youtubeUrl = `https://www.youtube.com/watch?v=${encodeURIComponent(videoId)}`;

  if (failed) {
    return (
      <div className="video-message" role="alert">
        <p>This video can't be played here.</p>
        <a href={youtubeUrl} target="_blank" rel="noreferrer">Watch it on YouTube</a>
      </div>
    );
  }
  return <div className="video-frame" ref={containerRef} />;
}
