import { useState } from 'react';
import aiMachineLearning from '../assets/courses/ai-machine-learning.jpg';
import appliedMathematics from '../assets/courses/applied-mathematics.jpg';
import businessManagement from '../assets/courses/business-management.jpg';
import cloudComputing from '../assets/courses/cloud-computing.jpg';
import cybersecurity from '../assets/courses/cybersecurity.jpg';
import databaseManagement from '../assets/courses/database-management.jpg';
import financialAccounting from '../assets/courses/financial-accounting.jpg';
import financialManagement from '../assets/courses/financial-management.jpg';
import fullStackWebDevelopment from '../assets/courses/full-stack-web-development.jpg';
import genetics from '../assets/courses/genetics.jpg';
import graphicDesign from '../assets/courses/graphic-design.jpg';
import humanAnatomy from '../assets/courses/human-anatomy.jpg';
import javaProgramming from '../assets/courses/java-programming.jpg';
import physics from '../assets/courses/physics.jpg';
import pythonProgramming from '../assets/courses/python-programming.jpg';
import Icon from './Icon';

// The picture for each course. The gradient and icon show if the image can't load.
// The first pattern matching the title or category wins, so order specific ones first.
const COVERS = [
  { pattern: /\bjava\b/i, image: javaProgramming, icon: 'cup', from: '#e76f2c', to: '#b3361e' },
  { pattern: /python/i, image: pythonProgramming, icon: 'terminal', from: '#2f7fc1', to: '#1f4e8c' },
  { pattern: /web|full stack/i, image: fullStackWebDevelopment, icon: 'globe', from: '#7c4dff', to: '#4527a0' },
  { pattern: /database|sql/i, image: databaseManagement, icon: 'database', from: '#00897b', to: '#00574f' },
  { pattern: /artificial|machine learning|\bai\b/i, image: aiMachineLearning, icon: 'chip', from: '#8e24aa', to: '#4a148c' },
  { pattern: /security/i, image: cybersecurity, icon: 'shield', from: '#37474f', to: '#102027' },
  { pattern: /cloud/i, image: cloudComputing, icon: 'cloud', from: '#29b6f6', to: '#0277bd' },
  { pattern: /business|commerce/i, image: businessManagement, icon: 'briefcase', from: '#c0862b', to: '#7a4f12' },
  { pattern: /accounting/i, image: financialAccounting, icon: 'calculator', from: '#2e9e6b', to: '#156042' },
  { pattern: /financ/i, image: financialManagement, icon: 'trend', from: '#0f9d9d', to: '#0a5f73' },
  { pattern: /anatomy|physiology/i, image: humanAnatomy, icon: 'heart', from: '#e53958', to: '#9c1236' },
  { pattern: /genetic|biology/i, image: genetics, icon: 'dna', from: '#43a047', to: '#1b5e20' },
  { pattern: /physics/i, image: physics, icon: 'atom', from: '#3f51b5', to: '#1a237e' },
  { pattern: /math/i, image: appliedMathematics, icon: 'sigma', from: '#f4a300', to: '#b36b00' },
  { pattern: /design/i, image: graphicDesign, icon: 'palette', from: '#ec407a', to: '#8e24aa' },
];

/** Courses with no match get a book, with a colour that stays the same for that course. */
function defaultCover(id) {
  const hue = (id * 47) % 360;
  return { icon: 'book', from: `hsl(${hue} 60% 50%)`, to: `hsl(${hue + 40} 60% 35%)` };
}

export default function CourseCover({ course }) {
  const [imageFailed, setImageFailed] = useState(false);
  const text = `${course.title} ${course.category ?? ''}`;
  const cover = COVERS.find((c) => c.pattern.test(text)) ?? defaultCover(course.id);
  return (
    <div className="course-cover" aria-hidden="true"
         style={{ background: `linear-gradient(135deg, ${cover.from}, ${cover.to})` }}>
      {cover.image && !imageFailed
        ? <img src={cover.image} alt="" loading="lazy" onError={() => setImageFailed(true)} />
        : <Icon name={cover.icon} size={56} />}
    </div>
  );
}
