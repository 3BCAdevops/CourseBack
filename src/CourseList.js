import React, { useEffect, useState } from "react";

const API_BASE = "http://localhost:8080";

function CourseList() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const studentId = 1;

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const res = await fetch(`${API_BASE}/api/courses`);
        const data = await res.json();
        setCourses(data);
      } catch (err) {
        setError("Failed to load courses");
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, []);

  const handleEnroll = async (courseId) => {
    try {
      const res = await fetch(`${API_BASE}/api/enrollments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          courseId: courseId,
          studentId: studentId,
        }),
      });

      if (!res.ok) {
        throw new Error("Enroll failed");
      }

      alert("Enrolled successfully!");
    } catch (err) {
      console.error(err);
      alert("Could not enroll. See console.");
    }
  };

  if (loading) return <p>Loading courses...</p>;
  if (error) return <p style={{ color: "red" }}>{error}</p>;

  return (
    <div style={{ padding: "1rem" }}>
      <h2>Available Courses</h2>
      {courses.length === 0 && <p>No courses found.</p>}

      <ul>
        {courses.map((c) => (
          <li key={c.id} style={{ marginBottom: "0.5rem" }}>
            <strong>{c.name}</strong> – {c.description}{" "}
            <button onClick={() => handleEnroll(c.id)}>Enroll</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default CourseList;
