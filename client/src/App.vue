<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Todo {
  id?: number
  title: string
  description: string
  completed: boolean
  dueDate: string | null
}

const API_BASE = 'http://localhost:8080/api/todos'

const todos = ref<Todo[]>([])
const loading = ref(false)
const error = ref<string | null>(null)
const editingTodo = ref<Todo | null>(null)
const isCreating = ref(false)

const newTodo = ref<Todo>({
  title: '',
  description: '',
  completed: false,
  dueDate: null
})

// Fetch all todos
const fetchTodos = async () => {
  loading.value = true
  error.value = null
  try {
    const response = await fetch(API_BASE)
    if (!response.ok) throw new Error('Failed to fetch todos')
    todos.value = await response.json()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred'
  } finally {
    loading.value = false
  }
}

// Create a new todo
const createTodo = async () => {
  if (!newTodo.value.title.trim()) {
    error.value = 'Title is required'
    return
  }
  
  loading.value = true
  error.value = null
  try {
    const response = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newTodo.value)
    })
    if (!response.ok) throw new Error('Failed to create todo')
    
    await fetchTodos()
    resetForm()
    isCreating.value = false
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred'
  } finally {
    loading.value = false
  }
}

// Update a todo
const updateTodo = async () => {
  if (!editingTodo.value || !editingTodo.value.id) return
  
  loading.value = true
  error.value = null
  try {
    const response = await fetch(`${API_BASE}/${editingTodo.value.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: editingTodo.value.title,
        description: editingTodo.value.description,
        dueDate: editingTodo.value.dueDate
      })
    })
    if (!response.ok) throw new Error('Failed to update todo')
    
    await fetchTodos()
    editingTodo.value = null
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred'
  } finally {
    loading.value = false
  }
}

// Toggle completion status
const toggleCompletion = async (todo: Todo) => {
  if (!todo.id) return
  
  loading.value = true
  error.value = null
  try {
    const response = await fetch(`${API_BASE}/${todo.id}/complete?completed=${!todo.completed}`, {
      method: 'PATCH'
    })
    if (!response.ok) throw new Error('Failed to toggle completion')
    
    await fetchTodos()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred'
  } finally {
    loading.value = false
  }
}

// Delete a todo
const deleteTodo = async (id: number) => {
  if (!confirm('Are you sure you want to delete this todo?')) return
  
  loading.value = true
  error.value = null
  try {
    const response = await fetch(`${API_BASE}/${id}`, {
      method: 'DELETE'
    })
    if (!response.ok) throw new Error('Failed to delete todo')
    
    await fetchTodos()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred'
  } finally {
    loading.value = false
  }
}

// Start editing a todo
const startEdit = (todo: Todo) => {
  editingTodo.value = { ...todo }
  isCreating.value = false
}

// Cancel editing
const cancelEdit = () => {
  editingTodo.value = null
}

// Reset form
const resetForm = () => {
  newTodo.value = {
    title: '',
    description: '',
    completed: false,
    dueDate: null
  }
}

// Start creating new todo
const startCreate = () => {
  isCreating.value = true
  editingTodo.value = null
  resetForm()
}

// Cancel create
const cancelCreate = () => {
  isCreating.value = false
  resetForm()
}

// Load todos on mount
onMounted(() => {
  fetchTodos()
})
</script>

<template>
  <div class="app-container">
    <header class="header">
      <h1>Tasks</h1>
      <button @click="startCreate" class="btn btn-primary" :disabled="isCreating">
        + New Task
      </button>
    </header>

    <div v-if="error" class="error-message">
      {{ error }}
      <button @click="error = null" class="close-btn">×</button>
    </div>

    <!-- Create Form -->
    <div v-if="isCreating" class="form-container">
      <h2>Create Task</h2>
      <form @submit.prevent="createTodo">
        <div class="form-group">
          <label>Title</label>
          <input 
            v-model="newTodo.title" 
            type="text" 
            placeholder="Enter task title"
            required
          />
        </div>
        <div class="form-group">
          <label>Description</label>
          <textarea 
            v-model="newTodo.description" 
            placeholder="Add details about this task..."
            maxlength="1000"
          ></textarea>
        </div>
        <div class="form-group">
          <label>Due Date</label>
          <input 
            v-model="newTodo.dueDate" 
            type="date" 
          />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-success" :disabled="loading">
            {{ loading ? 'Creating...' : 'Create Task' }}
          </button>
          <button type="button" @click="cancelCreate" class="btn btn-secondary" :disabled="loading">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- Edit Form -->
    <div v-if="editingTodo" class="form-container">
      <h2>Edit Task</h2>
      <form @submit.prevent="updateTodo">
        <div class="form-group">
          <label>Title</label>
          <input 
            v-model="editingTodo.title" 
            type="text" 
            placeholder="Enter task title"
            required
          />
        </div>
        <div class="form-group">
          <label>Description</label>
          <textarea 
            v-model="editingTodo.description" 
            placeholder="Add details about this task..."
            maxlength="1000"
          ></textarea>
        </div>
        <div class="form-group">
          <label>Due Date</label>
          <input 
            v-model="editingTodo.dueDate" 
            type="date" 
          />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-success" :disabled="loading">
            {{ loading ? 'Updating...' : 'Save Changes' }}
          </button>
          <button type="button" @click="cancelEdit" class="btn btn-secondary" :disabled="loading">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- Todos List -->
    <div class="todos-container">
      <div v-if="loading && todos.length === 0" class="loading">
        Loading tasks...
      </div>

      <div v-else-if="todos.length === 0" class="empty-state">
        <p>No tasks yet. Create your first one.</p>
      </div>

      <div v-else class="todos-grid">
        <div 
          v-for="todo in todos" 
          :key="todo.id" 
          class="todo-card"
          :class="{ completed: todo.completed }"
        >
          <div class="todo-header">
            <h3>{{ todo.title }}</h3>
            <div class="todo-actions">
              <button 
                @click="toggleCompletion(todo)" 
                class="btn-icon"
                :title="todo.completed ? 'Mark as incomplete' : 'Mark as complete'"
              >
                {{ todo.completed ? '✓' : '○' }}
              </button>
              <button 
                @click="startEdit(todo)" 
                class="btn-icon"
                title="Edit"
              >
                ✎
              </button>
              <button 
                @click="deleteTodo(todo.id!)" 
                class="btn-icon delete"
                title="Delete"
              >
                ×
              </button>
            </div>
          </div>
          
          <p class="todo-description">{{ todo.description || 'No description provided' }}</p>
          
          <div class="todo-footer">
            <span v-if="todo.dueDate" class="due-date">
              {{ new Date(todo.dueDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) }}
            </span>
            <span v-else class="due-date">No due date</span>
            <span class="status-badge" :class="todo.completed ? 'completed' : 'pending'">
              {{ todo.completed ? 'Done' : 'Active' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Geist:wght@300;400;500;600;700&display=swap');

* {
  box-sizing: border-box;
}

.app-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 24px;
  font-family: 'Geist', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #000000;
  min-height: 100vh;
  color: #ffffff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 48px;
  padding-bottom: 24px;
  border-bottom: 1px solid #222222;
}

.header h1 {
  margin: 0;
  color: #ffffff;
  font-size: 2.5rem;
  font-weight: 300;
  letter-spacing: -0.02em;
}

.error-message {
  background-color: #1a1a1a;
  border: 1px solid #333333;
  color: #ff4444;
  padding: 16px 20px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  font-weight: 400;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  color: #ff4444;
  padding: 0 8px;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.close-btn:hover {
  opacity: 1;
}

.form-container {
  background: #0a0a0a;
  padding: 32px;
  border-radius: 12px;
  margin-bottom: 40px;
  border: 1px solid #1a1a1a;
}

.form-container h2 {
  margin-top: 0;
  margin-bottom: 24px;
  color: #ffffff;
  font-size: 1.5rem;
  font-weight: 400;
  letter-spacing: -0.01em;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: #cccccc;
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #222222;
  border-radius: 8px;
  font-size: 15px;
  font-family: 'Geist', inherit;
  background: #000000;
  color: #ffffff;
  transition: border-color 0.2s;
  font-weight: 400;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #ffffff;
}

.form-group textarea {
  min-height: 120px;
  resize: vertical;
  line-height: 1.6;
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
}

.btn {
  padding: 12px 28px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  font-family: 'Geist', inherit;
  letter-spacing: 0.01em;
}

.btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #ffffff;
  color: #000000;
}

.btn-primary:hover:not(:disabled) {
  background-color: #e6e6e6;
}

.btn-success {
  background-color: #ffffff;
  color: #000000;
}

.btn-success:hover:not(:disabled) {
  background-color: #e6e6e6;
}

.btn-secondary {
  background-color: transparent;
  color: #ffffff;
  border: 1px solid #333333;
}

.btn-secondary:hover:not(:disabled) {
  border-color: #666666;
}

.loading, .empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666666;
  font-size: 1rem;
  font-weight: 400;
}

.todos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 20px;
}

.todo-card {
  background: #0a0a0a;
  border: 1px solid #1a1a1a;
  border-radius: 12px;
  padding: 24px;
  transition: border-color 0.2s;
}

.todo-card:hover {
  border-color: #2a2a2a;
}

.todo-card.completed {
  opacity: 0.5;
}

.todo-card.completed .todo-header h3 {
  text-decoration: line-through;
  opacity: 0.6;
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 16px;
  gap: 16px;
}

.todo-header h3 {
  margin: 0;
  color: #ffffff;
  font-size: 1.125rem;
  font-weight: 500;
  flex: 1;
  letter-spacing: -0.01em;
  line-height: 1.4;
}

.todo-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.1rem;
  padding: 6px;
  border-radius: 6px;
  transition: background 0.2s;
  color: #999999;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background: #1a1a1a;
  color: #ffffff;
}

.btn-icon.delete:hover {
  background: #1a0a0a;
  color: #ff4444;
}

.todo-description {
  color: #999999;
  margin: 0 0 16px 0;
  line-height: 1.6;
  min-height: 48px;
  font-size: 0.9375rem;
  font-weight: 400;
}

.todo-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #1a1a1a;
}

.due-date {
  font-size: 0.8125rem;
  color: #666666;
  font-weight: 500;
  letter-spacing: 0.01em;
}

.status-badge {
  padding: 5px 14px;
  border-radius: 6px;
  font-size: 0.75rem;
  font-weight: 500;
  letter-spacing: 0.03em;
  text-transform: uppercase;
}

.status-badge.completed {
  background: #0f1f0f;
  color: #4ade80;
  border: 1px solid #1a2a1a;
}

.status-badge.pending {
  background: #1a1a1a;
  color: #ffffff;
  border: 1px solid #2a2a2a;
}
</style>
