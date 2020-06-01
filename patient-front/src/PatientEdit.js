import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import { Button, Container, Form, FormGroup, Input, Label, Alert } from 'reactstrap';
import { instanceOf } from 'prop-types';
import { Cookies, withCookies } from 'react-cookie';
import AppNavbar from './NavBar';

class PatientEdit extends Component {
  static propTypes = {
    cookies: instanceOf(Cookies).isRequired
  };

  emptyItem = {
    id: '',
    firstName: '',
    lastName: '',
    patientId: '',
    gender: 'M',
    dob: '',
    middleName: '',
  };

  constructor(props) {
    super(props);
    const { cookies } = props;
   

    this.state = {
      item: this.emptyItem,
      errors: [],
      csrfToken: cookies.get('XSRF-TOKEN')
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  async componentDidMount() {
       console.log("props")
    console.log(this.props.readOnly)
    if (this.props.match.params.id !== 'new') {
      try {
        const patient = await (await fetch(`/patients/${this.props.match.params.id}`, { credentials: 'include' })).json();

        this.setState({ item: patient });
      } catch (error) {
        this.props.history.push('/');
      }
    }
  }

  handleChange(event) {
    const target = event.target;
    const value = target.value;
    const name = target.name;
    let item = { ...this.state.item };
    item[name] = value;
    this.setState({ item });
  }

  async handleSubmit(event) {
    event.preventDefault();
    const { item, csrfToken } = this.state;

    const patient = await (await fetch('/patients' + (item.id ? '/' + item.id : ''), {
      method: (item.id) ? 'PUT' : 'POST',
      headers: {
        'X-XSRF-TOKEN': csrfToken,
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(item),
      credentials: 'include'
    })).json();

    if (patient.status && patient.status >= 400 && patient.status <= 500) {
      this.setState({ errors: patient.messages })
    } else {
      this.props.history.push('/patients');
    }

  }

  render() {
    const { item, errors } = this.state;
    const { isReadOnly } = this.props;

    const title = <h2>{isReadOnly ? 'Patient Detail' : item.id ? 'Edit Patient' : 'Add Patient'}</h2>;

    return <div>
      <AppNavbar />
      <Container>
        {title}
        <Form onSubmit={this.handleSubmit} >
          {errors.length ?
            <div>
              {errors.map(error => {
                return <Alert color="danger">
                  {error}
                </Alert>
              })}

            </div>
            :
            null
          }
          <FormGroup>
            <Label for="patientId">Patient ID</Label>
            <Input type="text" readOnly={isReadOnly} name="patientId" id="patientId" value={item.patientId || ''}
              onChange={this.handleChange} autoComplete="patientId" />
          </FormGroup>
          <FormGroup>
            <Label for="firstName">First Name</Label>
            <Input type="text" readOnly={isReadOnly} name="firstName" id="firstName" value={item.firstName || ''}
              onChange={this.handleChange} autoComplete="firstName" />
          </FormGroup>
          <FormGroup>
            <Label for="lastName">Last Name</Label>
            <Input type="text" readOnly={isReadOnly} name="lastName" id="lastName" value={item.lastName || ''}
              onChange={this.handleChange} autoComplete="lastName" />
          </FormGroup>
          <FormGroup>
            <Label for="middleName">Middle Name</Label>
            <Input type="text" readOnly={isReadOnly} name="middleName" id="middleName" value={item.middleName || ''}
              onChange={this.handleChange} autoComplete="middleName" />
          </FormGroup>

          <FormGroup>
            <Label for="gender">Gender</Label>
            <Input type="select" readOnly={isReadOnly} name="gender" id="gender" onChange={this.handleChange} value={item.gender}>
              <option value="M" defaultChecked>Male</option>
              <option value="F">Female</option>
              <option value="O">Other</option>
            </Input>
          </FormGroup>
          <FormGroup>
            <Label for="dob">Date Of Birth</Label>
            <Input
              type="date"
              name="dob"
              id="dob"
              placeholder=""
              onChange={this.handleChange}
              value={item.dob}
              readOnly={isReadOnly}
            />
          </FormGroup>
          {
            isReadOnly ?
              <FormGroup>
                <Button color="primary" tag={Link} to="/">Back</Button>{' '}
              </FormGroup>
              :
              <FormGroup>
                <Button color="primary" type="submit">Save</Button>{' '}
                <Button color="secondary" tag={Link} to="/">Cancel</Button>
              </FormGroup>
          }
        </Form>
      </Container>
    </div>
  }
}

export default withCookies(withRouter(PatientEdit));