import React, { Component } from 'react';
import HeaderBar from './components/HeaderBar';
import NavigationMenu from './components/NavigationMenu';

import logo from './img/logo.svg';
import './css/App.css';

//npm start
class App extends Component {

  render() {
    return (
      <div className="App">       
        {/* Header */}
        <HeaderBar />

        {/* Menu */}
        <NavigationMenu />

        <main>
          <p>
            Front-end application (SPA)...
          </p>
        </main>       
        
        {/* Footer */}
        <footer>
          <img src={logo} className="App-logo" alt="logo" />
        </footer>

      </div>

    );
  }

}

export default App;