import Markdown from 'react-markdown';
import { Link, NavLink, useParams } from 'react-router-dom';
import { getCourse } from '../api/courses';
import { getModule, listModules } from '../api/modules';
import { useAuth } from '../auth/useAuth';
import YouTubePlayer from '../components/YouTubePlayer';
import { useAsync } from '../hooks/useAsync';

/** /courses/:id/learn/:moduleId - module list beside the video, with the lesson notes below it. */
export default function LearnPage() {
  const { id, moduleId } = useParams();
  const courseId = Number(id);
  const { token } = useAuth();

  const course = useAsync(() => getCourse(courseId, token), [courseId, token]);
  const outline = useAsync(() => listModules(courseId, token), [courseId, token]);

  const modules = outline.data ?? [];
  // No module in the URL: start with the first one
  const selectedId = moduleId ? Number(moduleId) : modules[0]?.id;
  const content = useAsync(
    () => (selectedId ? getModule(courseId, selectedId, token) : Promise.resolve(null)),
    [courseId, selectedId, token],
  );

  if (course.loading || outline.loading) return <p className="muted">Loading course…</p>;
  const loadError = course.error ?? outline.error;
  if (loadError) return <p className="alert alert-error" role="alert">{loadError.message}</p>;

  const index = modules.findIndex((m) => m.id === selectedId);
  const previous = modules[index - 1];
  const next = index >= 0 ? modules[index + 1] : undefined;
  const lesson = (m) => `/courses/${courseId}/learn/${m.id}`;

  return (
    <div className="learn">
      <aside className="learn-sidebar">
        <p className="learn-back"><Link to={`/courses/${courseId}`}>← Course details</Link></p>
        <h1 className="learn-course">{course.data.title}</h1>
        {course.data.description && <p className="muted small">{course.data.description}</p>}
        <nav aria-label="Course modules">
          <ol className="module-list">
            {modules.map((m) => (
              <li key={m.id}>
                {/* Highlighted by hand: the first module has no id in its URL at the start */}
                <NavLink to={lesson(m)} className={m.id === selectedId ? 'current' : undefined}
                         aria-current={m.id === selectedId ? 'page' : undefined}>
                  <span className="module-number">{m.position}</span>
                  <span>{m.title}</span>
                </NavLink>
              </li>
            ))}
          </ol>
        </nav>
      </aside>

      <section className="learn-main">
        {modules.length === 0 && <p className="muted">This course doesn't have any modules yet.</p>}

        {content.loading && selectedId && <p className="muted">Loading lesson…</p>}

        {content.error && (
          <div className="alert alert-error" role="alert">
            <p>{content.error.message}</p>
            {content.error.status === 403 && <Link to={`/courses/${courseId}`}>Go to the course page to enroll</Link>}
          </div>
        )}

        {content.data && !content.loading && (
          <>
            <p className="question-number">Module {content.data.position} of {modules.length}</p>
            <h2 className="learn-title">{content.data.title}</h2>

            {content.data.youtubeVideoId ? (
              <YouTubePlayer key={content.data.youtubeVideoId} videoId={content.data.youtubeVideoId}
                             title={content.data.title} />
            ) : (
              <div className="video-message">
                <p>A video for this module hasn't been added yet.</p>
              </div>
            )}

            <nav className="module-nav" aria-label="Previous and next module">
              {previous
                ? <Link to={lesson(previous)} className="button button-secondary">← Previous module</Link>
                : <span />}
              {next && <Link to={lesson(next)} className="button">Next module →</Link>}
            </nav>

            {content.data.notes && (
              <article className="notes">
                <h3 className="notes-heading">Lesson notes</h3>
                {/* Notes are Markdown from the database. react-markdown doesn't render raw HTML */}
                <Markdown>{content.data.notes}</Markdown>
              </article>
            )}
          </>
        )}
      </section>
    </div>
  );
}
