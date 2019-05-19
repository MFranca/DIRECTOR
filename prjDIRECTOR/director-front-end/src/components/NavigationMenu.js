import React, {Component} from 'react';

class NavigationMenu extends Component {

    render() {
        return (
            <aside className="main-sidebar">
                {/* sidebar: style can be found in sidebar.less */}
                <section className="sidebar" >                    
                    {/* sidebar menu: : style can be found in sidebar.less */}
                    <ul className="sidebar-menu">
                        <li className="header">TOOL FEATURES</li>
                       
                        <li><i className="fa fa-book"></i> <span>Documentation</span></li>                        
                        <li><a href="#"><i className="fa fa-circle-o text-red"></i> <span>Overall Status</span></a></li>
                        <li><a href="#"><i className="fa fa-circle-o text-yellow"></i> <span>Registered PaaS</span></a></li>
                        <li><a href="#"><i className="fa fa-circle-o text-aqua"></i> <span>Available Features</span></a></li>
                    </ul>
                </section>
                {/* /.sidebar */}
            </aside>
        )
    }     
}

export default NavigationMenu;