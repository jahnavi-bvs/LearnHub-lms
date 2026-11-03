import Icon from './Icon';

// A generated picture for each course: gradient colours plus an icon.
// The first pattern matching the title or category wins, so order specific ones first.
const COVERS = [
  { pattern: /\bjava\b/i, icon: 'cup', from: '#e76f2c', to: '#b3361e' },
  { pattern: /python/i, icon: 'terminal', from: '#2f7fc1', to: '#1f4e8c' },
  { pattern: /web|full stack/i, icon: 'globe', from: '#7c4dff', to: '#4527a0' },
  { pattern: /database|sql/i, icon: 'database', from: '#00897b', to: '#00574f' },
  { pattern: /artificial|machine learning|\bai\b/i, icon: 'chip', from: '#8e24aa', to: '#4a148c' },
  { pattern: /security/i, icon: 'shield', from: '#37474f', to: '#102027' },
  { pattern: /cloud/i, icon: 'cloud', from: '#29b6f6', to: '#0277bd' },
  { pattern: /business|commerce/i, icon: 'briefcase', from: '#c0862b', to: '#7a4f12' },
  { pattern: /accounting/i, icon: 'calculator', from: '#2e9e6b', to: '#156042' },
  { pattern: /financ/i, icon: 'trend', from: '#0f9d9d', to: '#0a5f73' },
  { pattern: /anatomy|physiology/i, icon: 'heart', from: '#e53958', to: '#9c1236' },
  { pattern: /genetic|biology/i, icon: 'dna', from: '#43a047', to: '#1b5e20' },
  { pattern: /physics/i, icon: 'atom', from: '#3f51b5', to: '#1a237e' },
  { pattern: /math/i, icon: 'sigma', from: '#f4a300', to: '#b36b00' },
  { pattern: /design/i, icon: 'palette', from: '#ec407a', to: '#8e24aa' },
];

/** Courses with no match get a book, with a colour that stays the same for that course. */
function defaultCover(id) {
  const hue = (id * 47) % 360;
  return { icon: 'book', from: `hsl(${hue} 60% 50%)`, to: `hsl(${hue + 40} 60% 35%)` };
}

export default function CourseCover({ course }) {
  const text = `${course.title} ${course.category ?? ''}`;
  const cover = COVERS.find((c) => c.pattern.test(text)) ?? defaultCover(course.id);
  return (
    <div className="course-cover" aria-hidden="true"
         style={{ background: `linear-gradient(135deg, ${cover.from}, ${cover.to})` }}>
      <Icon name={cover.icon} size={56} />
    </div>
  );
}
