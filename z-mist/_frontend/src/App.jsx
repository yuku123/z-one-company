import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import SecretList from './pages/secret/SecretList'
import SecretEdit from './pages/secret/SecretEdit'
import Login from './pages/login/index'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/secret" replace />} />
          <Route path="secret" element={<SecretList />} />
          <Route path="secret/edit/:id" element={<SecretEdit />} />
          <Route path="secret/add" element={<SecretEdit />} />
        </Route>
      </Routes>
    </Router>
  )
}

export default App