import React, {Component} from 'react';

class HeaderBar extends Component {

    render() {
        return (            
        <header className="main-header">
            {/* Logo */}
            <a href="/" className="logo">
                {/* mini logo for sidebar mini 50x50 pixels */}
                <span className="logo-mini font-weight-bold">D</span>
                {/* logo for regular state and mobile devices */}
                <span className="logo-lg font-weight-bold">DIRECTOR front-end</span>
            </a>
                        
            <nav className="navbar navbar-static-top">                
                <div className="navbar-custom-menu">
                    <ul className="nav navbar-nav">                        
                    </ul>
                </div>
            </nav>
        </header>
        );
    }
}

export default HeaderBar;