import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import HamburgerMenu from './HamburgerMenu';
import './Layout.css';

const Layout = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="app-layout">
      <header className="app-header">
        <button
          className="hamburger-btn"
          onClick={() => setMenuOpen(true)}
          aria-label="Abrir menú"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="3" y1="6" x2="21" y2="6" />
            <line x1="3" y1="12" x2="21" y2="12" />
            <line x1="3" y1="18" x2="21" y2="18" />
          </svg>
        </button>

        <div className="header-logo">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
            <path d="M12 2L2 7l10 5 10-5-10-5z" />
            <path d="M2 17l10 5 10-5" />
            <path d="M2 12l10 5 10-5" />
          </svg>
          <span>Portal Gubernamental</span>
        </div>

        <div className="header-spacer" />
      </header>

      <HamburgerMenu
        isOpen={menuOpen}
        onClose={() => setMenuOpen(false)}
      />

      <main className="app-main">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
