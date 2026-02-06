import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import AdminLayout from '@/shared/components/layout/AdminLayout';
import DashboardPage from '@/pages/dashboard/DashboardPage';
import BudgetPage from '@/pages/budget/BudgetPage';
import ProductPage from '@/pages/product/ProductPage';
import OrderPage from '@/pages/order/OrderPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<AdminLayout />}>
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<DashboardPage />} />
          <Route path="/budget" element={<BudgetPage />} />
          <Route path="/products" element={<ProductPage />} />
          <Route path="/orders" element={<OrderPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
