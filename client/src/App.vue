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
      <h1>📝 Todo Manager</h1>
      <button @click="startCreate" class="btn btn-primary" :disabled="isCreating">
        ➕ New Todo
      </button>
    </header>

    <div v-if="error" class="error-message">
      ⚠️ {{ error }}
      <button @click="error = null" class="close-btn">✖</button>
    </div>

    <!-- Create Form -->
    <div v-if="isCreating" class="form-container">
      <h2>Create New Todo</h2>
      <form @submit.prevent="createTodo">
        <div class="form-group">
          <label>Title *</label>
          <input 
            v-model="newTodo.title" 
            type="text" 
            placeholder="Enter todo title"
            required
          />
        </div>
        <div class="form-group">
          <label>Description</label>
          <textarea 
            v-model="newTodo.description" 
            placeholder="Enter description (max 1000 characters)"
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
            {{ loading ? 'Creating...' : 'Create' }}
          </button>
          <button type="button" @click="cancelCreate" class="btn btn-secondary" :disabled="loading">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- Edit Form -->
    <div v-if="editingTodo" class="form-container">
      <h2>Edit Todo</h2>
      <form @submit.prevent="updateTodo">
        <div class="form-group">
          <label>Title *</label>
          <input 
            v-model="editingTodo.title" 
            type="text" 
            placeholder="Enter todo title"
            required
          />
        </div>
        <div class="form-group">
          <label>Description</label>
          <textarea 
            v-model="editingTodo.description" 
            placeholder="Enter description (max 1000 characters)"
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
            {{ loading ? 'Updating...' : 'Update' }}
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
        Loading todos...
      </div>

      <div v-else-if="todos.length === 0" class="empty-state">
        <p>No todos yet. Create your first one!</p>
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
                🗑
              </button>
            </div>
          </div>
          
          <p class="todo-description">{{ todo.description || 'No description' }}</p>
          
          <div class="todo-footer">
            <span v-if="todo.dueDate" class="due-date">
              📅 {{ new Date(todo.dueDate).toLocaleDateString() }}
            </span>
            <span class="status-badge" :class="todo.completed ? 'completed' : 'pending'">
              {{ todo.completed ? 'Completed' : 'Pending' }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
* {
  box-sizing: border-box;
}

.app-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e0e0e0;
}

.header h1 {
  margin: 0;
  color: #333;
  font-size: 2rem;
}

.error-message {
  background-color: #fee;
  border: 1px solid #fcc;
  color: #c33;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  color: #c33;
  padding: 0 5px;
}

.form-container {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-container h2 {
  margin-top: 0;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-success:hover:not(:disabled) {
  background-color: #218838;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #5a6268;
}

.loading, .empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 1.1rem;
}

.todos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.todo-card {
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.todo-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.todo-card.completed {
  opacity: 0.7;
  background: #f8f9fa;
}

.todo-header {
  display: flex;
  justify-content: space-between;
  align-items: start;
  margin-bottom: 12px;
}

.todo-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.2rem;
  flex: 1;
}

.todo-actions {
  display: flex;
  gap: 8px;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.btn-icon:hover {
  background: #f0f0f0;
}

.btn-icon.delete:hover {
  background: #fee;
  color: #c33;
}

.todo-description {
  color: #666;
  margin: 12px 0;
  line-height: 1.5;
  min-height: 40px;
}

.todo-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #e0e0e0;
}

.due-date {
  font-size: 0.9rem;
  color: #666;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}

.status-badge.completed {
  background: #d4edda;
  color: #155724;
}

.status-badge.pending {
  background: #fff3cd;
  color: #856404;
}
</style>
