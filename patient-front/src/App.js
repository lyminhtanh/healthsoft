import React, { Component } from 'react';
import './App.css';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import { CookiesProvider } from 'react-cookie';
import PatientList from './PatientList';
import PatientEdit from './PatientEdit';

class App extends Component {
  render() {
    return (
      <CookiesProvider>
        <Router>
          <Switch>
            <Route path='/' exact={true} component={PatientList}/>
            <Route path='/patients' exact={true} component={PatientList}/>
            <Route path='/patients/:id' component={PatientEdit}/>
          </Switch>
        </Router>
      </CookiesProvider>
    )
  }
}

export default App;